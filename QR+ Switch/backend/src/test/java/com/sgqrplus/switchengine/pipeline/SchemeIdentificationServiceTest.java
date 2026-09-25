package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.exception.SchemeNotRecognizedException;
import com.sgqrplus.switchengine.repository.SchemeRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchemeIdentificationServiceTest {

    @Test
    void recognizedPrefix_resolvesToScheme() {
        SchemeRepository schemeRepository = mock(SchemeRepository.class);
        Scheme payNow = new Scheme("PAYNOW_QR", "PayNow QR", "pattern", "PAYNOW_JSON_V1");
        when(schemeRepository.findById("PAYNOW_QR")).thenReturn(Optional.of(payNow));

        SchemeIdentificationService service = new SchemeIdentificationService(schemeRepository);

        Scheme resolved = service.identify("PAYNOW_QR|merchant123");

        assertThat(resolved.getCode()).isEqualTo("PAYNOW_QR");
    }

    @Test
    void unrecognizedPrefix_throws() {
        SchemeRepository schemeRepository = mock(SchemeRepository.class);
        when(schemeRepository.findById("UNKNOWN")).thenReturn(Optional.empty());
        SchemeIdentificationService service = new SchemeIdentificationService(schemeRepository);

        assertThatThrownBy(() -> service.identify("UNKNOWN|merchant123"))
                .isInstanceOf(SchemeNotRecognizedException.class);
    }

    @Test
    void blankPayload_throws() {
        SchemeRepository schemeRepository = mock(SchemeRepository.class);
        SchemeIdentificationService service = new SchemeIdentificationService(schemeRepository);

        assertThatThrownBy(() -> service.identify("  "))
                .isInstanceOf(SchemeNotRecognizedException.class);
    }
}
