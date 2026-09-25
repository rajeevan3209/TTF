package com.sgqrplus.switchengine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Off-us interchange fee configuration. feePercentage is expressed as a
 * fraction (e.g. 0.005 = 0.5%) and must fall within the 0.3%-0.7% band.
 * feeCap is the maximum fee (in SGD) that can be charged on a single
 * off-us transaction, regardless of the percentage calculation.
 */
@Entity
@Table(name = "fee_config")
public class FeeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tier;

    @Column(name = "fee_percentage", nullable = false, precision = 6, scale = 5)
    private BigDecimal feePercentage;

    @Column(name = "fee_cap", nullable = false, precision = 12, scale = 2)
    private BigDecimal feeCap;

    protected FeeConfig() {
    }

    public FeeConfig(String tier, BigDecimal feePercentage, BigDecimal feeCap) {
        this.tier = tier;
        this.feePercentage = feePercentage;
        this.feeCap = feeCap;
    }

    public Long getId() {
        return id;
    }

    public String getTier() {
        return tier;
    }

    public BigDecimal getFeePercentage() {
        return feePercentage;
    }

    public BigDecimal getFeeCap() {
        return feeCap;
    }
}
