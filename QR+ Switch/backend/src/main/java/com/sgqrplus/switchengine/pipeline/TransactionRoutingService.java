package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.FeeConfig;
import com.sgqrplus.switchengine.domain.Participant;
import com.sgqrplus.switchengine.repository.FeeConfigRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Stage C: Transaction Routing.
 *
 * Determines on-us vs off-us based on whether the acquirer and issuer
 * belong to the same scheme/network, applies the off-us interchange fee
 * (0.3%-0.7%, configured via FeeConfig, capped at FeeConfig.feeCap per
 * transaction), and resolves the destination acquirer switch the
 * transaction should be delivered to.
 */
@Service
public class TransactionRoutingService {

    private static final String DEFAULT_FEE_TIER = "STANDARD";
    private static final BigDecimal DEFAULT_FEE_PERCENTAGE = new BigDecimal("0.005");
    private static final BigDecimal DEFAULT_FEE_CAP = new BigDecimal("100.00");

    private final FeeConfigRepository feeConfigRepository;

    public TransactionRoutingService(FeeConfigRepository feeConfigRepository) {
        this.feeConfigRepository = feeConfigRepository;
    }

    public RoutingDecision route(Participant acquirer, Participant issuer, BigDecimal amount) {
        boolean onUs = acquirer.getScheme().getCode().equals(issuer.getScheme().getCode());
        String destinationSchemeCode = acquirer.getScheme().getCode();

        if (onUs) {
            return new RoutingDecision(true, BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), destinationSchemeCode);
        }

        FeeConfig feeConfig = feeConfigRepository.findByTier(DEFAULT_FEE_TIER).orElse(null);
        BigDecimal feePercentage = feeConfig != null ? feeConfig.getFeePercentage() : DEFAULT_FEE_PERCENTAGE;
        BigDecimal feeCap = feeConfig != null ? feeConfig.getFeeCap() : DEFAULT_FEE_CAP;

        BigDecimal feeAmount = amount.multiply(feePercentage).setScale(4, RoundingMode.HALF_UP);
        feeAmount = feeAmount.min(feeCap.setScale(4, RoundingMode.HALF_UP));
        return new RoutingDecision(false, feeAmount, destinationSchemeCode);
    }
}
