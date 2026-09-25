package com.sgqrplus.switchengine.controller;

import com.sgqrplus.switchengine.domain.Transaction;
import com.sgqrplus.switchengine.dto.InboundTransactionRequest;
import com.sgqrplus.switchengine.dto.TransactionResponse;
import com.sgqrplus.switchengine.pipeline.MessageTranslationService;
import com.sgqrplus.switchengine.repository.SchemeRepository;
import com.sgqrplus.switchengine.repository.TransactionRepository;
import com.sgqrplus.switchengine.service.TransactionProcessingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionProcessingService transactionProcessingService;
    private final TransactionRepository transactionRepository;
    private final SchemeRepository schemeRepository;
    private final MessageTranslationService messageTranslationService;

    public TransactionController(TransactionProcessingService transactionProcessingService,
                                  TransactionRepository transactionRepository,
                                  SchemeRepository schemeRepository,
                                  MessageTranslationService messageTranslationService) {
        this.transactionProcessingService = transactionProcessingService;
        this.transactionRepository = transactionRepository;
        this.schemeRepository = schemeRepository;
        this.messageTranslationService = messageTranslationService;
    }

    @PostMapping("/inbound")
    public TransactionResponse submitInbound(@Valid @RequestBody InboundTransactionRequest request) {
        return transactionProcessingService.process(request);
    }

    @GetMapping
    public List<TransactionResponse> listTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransaction(@PathVariable Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found: " + id));
        return toResponse(tx);
    }

    private TransactionResponse toResponse(Transaction tx) {
        var destinationScheme = schemeRepository.findById(tx.getDestinationSchemeCode()).orElseThrow();
        String translatedMessage = messageTranslationService.translate(tx, destinationScheme);
        return TransactionResponse.from(tx, translatedMessage);
    }
}
