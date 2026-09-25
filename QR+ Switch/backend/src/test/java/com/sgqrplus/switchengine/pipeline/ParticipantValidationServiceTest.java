package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.Participant;
import com.sgqrplus.switchengine.domain.ParticipantStatus;
import com.sgqrplus.switchengine.domain.ParticipantType;
import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.exception.ParticipantNotActiveException;
import com.sgqrplus.switchengine.exception.ParticipantNotFoundException;
import com.sgqrplus.switchengine.repository.ParticipantRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParticipantValidationServiceTest {

    private final Scheme payNow = new Scheme("PAYNOW_QR", "PayNow QR", "pattern", "PAYNOW_JSON_V1");

    @Test
    void activeParticipant_passesValidation() {
        ParticipantRepository repo = mock(ParticipantRepository.class);
        Participant participant = new Participant("Active Bank", payNow, ParticipantType.ISSUER, ParticipantStatus.ACTIVE);
        when(repo.findById(1L)).thenReturn(Optional.of(participant));

        ParticipantValidationService service = new ParticipantValidationService(repo);

        assertThat(service.validate(1L, "Issuer")).isSameAs(participant);
    }

    @Test
    void missingParticipant_throwsNotFound() {
        ParticipantRepository repo = mock(ParticipantRepository.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        ParticipantValidationService service = new ParticipantValidationService(repo);

        assertThatThrownBy(() -> service.validate(99L, "Acquirer"))
                .isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void suspendedParticipant_throwsNotActive() {
        ParticipantRepository repo = mock(ParticipantRepository.class);
        Participant participant = new Participant("Suspended Bank", payNow, ParticipantType.ISSUER, ParticipantStatus.SUSPENDED);
        when(repo.findById(2L)).thenReturn(Optional.of(participant));
        ParticipantValidationService service = new ParticipantValidationService(repo);

        assertThatThrownBy(() -> service.validate(2L, "Issuer"))
                .isInstanceOf(ParticipantNotActiveException.class);
    }
}
