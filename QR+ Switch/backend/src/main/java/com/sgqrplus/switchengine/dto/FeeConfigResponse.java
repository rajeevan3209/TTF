package com.sgqrplus.switchengine.dto;

import com.sgqrplus.switchengine.domain.FeeConfig;

import java.math.BigDecimal;

public class FeeConfigResponse {

    private String tier;
    private BigDecimal feePercentage;
    private BigDecimal feeCap;

    public static FeeConfigResponse from(FeeConfig feeConfig) {
        FeeConfigResponse r = new FeeConfigResponse();
        r.tier = feeConfig.getTier();
        r.feePercentage = feeConfig.getFeePercentage();
        r.feeCap = feeConfig.getFeeCap();
        return r;
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
