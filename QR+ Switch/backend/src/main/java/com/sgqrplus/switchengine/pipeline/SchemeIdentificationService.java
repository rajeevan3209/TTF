package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.exception.SchemeNotRecognizedException;
import com.sgqrplus.switchengine.repository.SchemeRepository;
import org.springframework.stereotype.Service;

/**
 * Stage A: Scheme Identification.
 *
 * Resolves which QR scheme an inbound payload belongs to. Real EMVCo QR
 * payloads are TLV-encoded; for this prototype we recognize a scheme by a
 * simple prefix convention: "<SCHEME_CODE>|<merchant/payload data>".
 */
@Service
public class SchemeIdentificationService {

    private final SchemeRepository schemeRepository;

    public SchemeIdentificationService(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    public Scheme identify(String qrPayload) {
        if (qrPayload == null || qrPayload.isBlank()) {
            throw new SchemeNotRecognizedException("QR payload is empty");
        }
        String prefix = qrPayload.contains("|") ? qrPayload.substring(0, qrPayload.indexOf('|')) : qrPayload;
        return schemeRepository.findById(prefix.trim().toUpperCase())
                .orElseThrow(() -> new SchemeNotRecognizedException(
                        "Unable to identify scheme from QR payload prefix: " + prefix));
    }
}
