package com.sgqrplus.switchengine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "sgqr_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_payload", nullable = false, length = 500)
    private String qrPayload;

    @ManyToOne(optional = false)
    private Participant acquirer;

    @ManyToOne(optional = false)
    private Participant issuer;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "on_us", nullable = false)
    private boolean onUs;

    @Column(name = "fee_amount", nullable = false, precision = 12, scale = 4)
    private BigDecimal feeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "destination_scheme_code", nullable = false, length = 30)
    private String destinationSchemeCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Transaction() {
    }

    public Transaction(String qrPayload, Participant acquirer, Participant issuer, BigDecimal amount,
                        boolean onUs, BigDecimal feeAmount, TransactionStatus status, String destinationSchemeCode) {
        this.qrPayload = qrPayload;
        this.acquirer = acquirer;
        this.issuer = issuer;
        this.amount = amount;
        this.onUs = onUs;
        this.feeAmount = feeAmount;
        this.status = status;
        this.destinationSchemeCode = destinationSchemeCode;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getQrPayload() {
        return qrPayload;
    }

    public Participant getAcquirer() {
        return acquirer;
    }

    public Participant getIssuer() {
        return issuer;
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

    public TransactionStatus getStatus() {
        return status;
    }

    public String getDestinationSchemeCode() {
        return destinationSchemeCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
