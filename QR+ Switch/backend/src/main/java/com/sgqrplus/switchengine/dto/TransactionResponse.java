package com.sgqrplus.switchengine.dto;

import com.sgqrplus.switchengine.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private Long id;
    private String qrPayload;
    private Long acquirerParticipantId;
    private String acquirerParticipantName;
    private String acquirerSchemeCode;
    private Long issuerParticipantId;
    private String issuerParticipantName;
    private String issuerSchemeCode;
    private BigDecimal amount;
    private boolean onUs;
    private BigDecimal feeAmount;
    private String destinationSchemeCode;
    private String translatedMessage;
    private String status;
    private Instant createdAt;

    public static TransactionResponse from(Transaction tx, String translatedMessage) {
        TransactionResponse r = new TransactionResponse();
        r.id = tx.getId();
        r.qrPayload = tx.getQrPayload();
        r.acquirerParticipantId = tx.getAcquirer().getId();
        r.acquirerParticipantName = tx.getAcquirer().getName();
        r.acquirerSchemeCode = tx.getAcquirer().getScheme().getCode();
        r.issuerParticipantId = tx.getIssuer().getId();
        r.issuerParticipantName = tx.getIssuer().getName();
        r.issuerSchemeCode = tx.getIssuer().getScheme().getCode();
        r.amount = tx.getAmount();
        r.onUs = tx.isOnUs();
        r.feeAmount = tx.getFeeAmount();
        r.destinationSchemeCode = tx.getDestinationSchemeCode();
        r.translatedMessage = translatedMessage;
        r.status = tx.getStatus().name();
        r.createdAt = tx.getCreatedAt();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getQrPayload() {
        return qrPayload;
    }

    public Long getAcquirerParticipantId() {
        return acquirerParticipantId;
    }

    public String getAcquirerParticipantName() {
        return acquirerParticipantName;
    }

    public String getAcquirerSchemeCode() {
        return acquirerSchemeCode;
    }

    public Long getIssuerParticipantId() {
        return issuerParticipantId;
    }

    public String getIssuerParticipantName() {
        return issuerParticipantName;
    }

    public String getIssuerSchemeCode() {
        return issuerSchemeCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isOnUs() {
        return onUs;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public String getDestinationSchemeCode() {
        return destinationSchemeCode;
    }

    public String getTranslatedMessage() {
        return translatedMessage;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
