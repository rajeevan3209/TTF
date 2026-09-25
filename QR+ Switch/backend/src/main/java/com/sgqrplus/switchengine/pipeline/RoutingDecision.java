package com.sgqrplus.switchengine.pipeline;

import java.math.BigDecimal;

/**
 * Outcome of Stage C (Transaction Routing): whether the transaction is
 * on-us or off-us, the fee applied (zero for on-us), and the destination
 * scheme the transaction should be dispatched to.
 */
public record RoutingDecision(boolean onUs, BigDecimal feeAmount, String destinationSchemeCode) {
}
