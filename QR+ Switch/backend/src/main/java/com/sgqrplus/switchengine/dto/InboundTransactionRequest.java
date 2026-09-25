package com.sgqrplus.switchengine.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class InboundTransactionRequest {

    @NotBlank
    private String qrPayload;

    @NotNull
    private Long acquirerParticipantId;

    @NotNull
    private Long issuerParticipantId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    public String getQrPayload() {
        return qrPayload;
    }

    public void setQrPayload(String qrPayload) {
        this.qrPayload = qrPayload;
    }

    public Long getAcquirerParticipantId() {
        return acquirerParticipantId;
    }

    public void setAcquirerParticipantId(Long acquirerParticipantId) {
        this.acquirerParticipantId = acquirerParticipantId;
    }

    public Long getIssuerParticipantId() {
        return issuerParticipantId;
    }

    public void setIssuerParticipantId(Long issuerParticipantId) {
        this.issuerParticipantId = issuerParticipantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
