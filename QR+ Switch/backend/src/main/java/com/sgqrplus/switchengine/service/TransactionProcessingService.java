package com.sgqrplus.switchengine.service;

import com.sgqrplus.switchengine.domain.Participant;
import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.domain.Transaction;
import com.sgqrplus.switchengine.domain.TransactionStatus;
import com.sgqrplus.switchengine.dto.InboundTransactionRequest;
import com.sgqrplus.switchengine.dto.TransactionResponse;
import com.sgqrplus.switchengine.pipeline.MessageTranslationService;
import com.sgqrplus.switchengine.pipeline.ParticipantValidationService;
import com.sgqrplus.switchengine.pipeline.RoutingDecision;
import com.sgqrplus.switchengine.pipeline.SchemeIdentificationService;
import com.sgqrplus.switchengine.pipeline.TransactionRoutingService;
import com.sgqrplus.switchengine.repository.SchemeRepository;
import com.sgqrplus.switchengine.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the four-stage SGQR+ national routing pipeline for every
 * inbound QR transaction: Scheme Identification -> Participant Validation
 * -> Transaction Routing -> Message Translation.
 */
@Service
public class TransactionProcessingService {

    private final SchemeIdentificationService schemeIdentificationService;
    private final ParticipantValidationService participantValidationService;
    private final TransactionRoutingService transactionRoutingService;
    private final MessageTranslationService messageTranslationService;
    private final TransactionRepository transactionRepository;
    private final SchemeRepository schemeRepository;

    public TransactionProcessingService(SchemeIdentificationService schemeIdentificationService,
                                         ParticipantValidationService participantValidationService,
                                         TransactionRoutingService transactionRoutingService,
                                         MessageTranslationService messageTranslationService,
                                         TransactionRepository transactionRepository,
                                         SchemeRepository schemeRepository) {
        this.schemeIdentificationService = schemeIdentificationService;
        this.participantValidationService = participantValidationService;
        this.transactionRoutingService = transactionRoutingService;
        this.messageTranslationService = messageTranslationService;
        this.transactionRepository = transactionRepository;
        this.schemeRepository = schemeRepository;
    }

    @Transactional
    public TransactionResponse process(InboundTransactionRequest request) {
        // Stage A: Scheme Identification (confirms the QR payload maps to a known scheme)
        schemeIdentificationService.identify(request.getQrPayload());

        // Stage B: Participant Validation
        Participant acquirer = participantValidationService.validate(request.getAcquirerParticipantId(), "Acquirer");
        Participant issuer = participantValidationService.validate(request.getIssuerParticipantId(), "Issuer");

        // Stage C: Transaction Routing (on-us/off-us + fee)
        RoutingDecision decision = transactionRoutingService.route(acquirer, issuer, request.getAmount());

        Transaction transaction = new Transaction(
                request.getQrPayload(),
                acquirer,
                issuer,
                request.getAmount(),
                decision.onUs(),
                decision.feeAmount(),
                TransactionStatus.APPROVED,
                decision.destinationSchemeCode());
        transaction = transactionRepository.save(transaction);

        // Stage D: Message Translation
        Scheme destinationScheme = schemeRepository.findById(decision.destinationSchemeCode())
                .orElseThrow();
        String translatedMessage = messageTranslationService.translate(transaction, destinationScheme);

        return TransactionResponse.from(transaction, translatedMessage);
    }
}
