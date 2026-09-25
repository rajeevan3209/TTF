package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.FeeConfig;
import com.sgqrplus.switchengine.domain.Participant;
import com.sgqrplus.switchengine.domain.ParticipantStatus;
import com.sgqrplus.switchengine.domain.ParticipantType;
import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.repository.FeeConfigRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionRoutingServiceTest {

    private final Scheme payNow = new Scheme("PAYNOW_QR", "PayNow QR", "pattern", "PAYNOW_JSON_V1");
    private final Scheme nets = new Scheme("NETS_QR", "NETS QR", "pattern", "NETS_ISO8583_LITE");

    @Test
    void sameSchemeParticipants_areRoutedOnUs_withNoFee() {
        FeeConfigRepository feeConfigRepository = mock(FeeConfigRepository.class);
        TransactionRoutingService service = new TransactionRoutingService(feeConfigRepository);

        Participant acquirer = new Participant("Acquirer A", payNow, ParticipantType.ACQUIRER, ParticipantStatus.ACTIVE);
        Participant issuer = new Participant("Issuer A", payNow, ParticipantType.ISSUER, ParticipantStatus.ACTIVE);

        RoutingDecision decision = service.route(acquirer, issuer, new BigDecimal("100.00"));

        assertThat(decision.onUs()).isTrue();
        assertThat(decision.feeAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(decision.destinationSchemeCode()).isEqualTo("PAYNOW_QR");
    }

    @Test
    void differentSchemeParticipants_areRoutedOffUs_withFeeWithinBand() {
        FeeConfigRepository feeConfigRepository = mock(FeeConfigRepository.class);
        when(feeConfigRepository.findByTier("STANDARD"))
                .thenReturn(Optional.of(new FeeConfig("STANDARD", new BigDecimal("0.005"), new BigDecimal("100.00"))));
        TransactionRoutingService service = new TransactionRoutingService(feeConfigRepository);

        Participant acquirer = new Participant("NETS Acquirer", nets, ParticipantType.ACQUIRER, ParticipantStatus.ACTIVE);
        Participant issuer = new Participant("PayNow Issuer", payNow, ParticipantType.ISSUER, ParticipantStatus.ACTIVE);

        RoutingDecision decision = service.route(acquirer, issuer, new BigDecimal("100.00"));

        assertThat(decision.onUs()).isFalse();
        // 100.00 * 0.5% = 0.50
        assertThat(decision.feeAmount()).isEqualByComparingTo(new BigDecimal("0.5000"));
        BigDecimal feePct = decision.feeAmount().divide(new BigDecimal("100.00"), 5, java.math.RoundingMode.HALF_UP);
        assertThat(feePct).isBetween(new BigDecimal("0.003"), new BigDecimal("0.007"));
        assertThat(decision.destinationSchemeCode()).isEqualTo("NETS_QR");
    }

    @Test
    void largeOffUsTransaction_feeIsCappedAtConfiguredLimit() {
        FeeConfigRepository feeConfigRepository = mock(FeeConfigRepository.class);
        when(feeConfigRepository.findByTier("STANDARD"))
                .thenReturn(Optional.of(new FeeConfig("STANDARD", new BigDecimal("0.005"), new BigDecimal("100.00"))));
        TransactionRoutingService service = new TransactionRoutingService(feeConfigRepository);

        Participant acquirer = new Participant("NETS Acquirer", nets, ParticipantType.ACQUIRER, ParticipantStatus.ACTIVE);
        Participant issuer = new Participant("PayNow Issuer", payNow, ParticipantType.ISSUER, ParticipantStatus.ACTIVE);

        // 100,000.00 * 0.5% = 500.00, which exceeds the 100.00 cap
        RoutingDecision decision = service.route(acquirer, issuer, new BigDecimal("100000.00"));

        assertThat(decision.onUs()).isFalse();
        assertThat(decision.feeAmount()).isEqualByComparingTo(new BigDecimal("100.0000"));
    }
}
