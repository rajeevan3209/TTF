package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.Participant;
import com.sgqrplus.switchengine.domain.ParticipantStatus;
import com.sgqrplus.switchengine.exception.ParticipantNotActiveException;
import com.sgqrplus.switchengine.exception.ParticipantNotFoundException;
import com.sgqrplus.switchengine.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

/**
 * Stage B: Participant Validation.
 *
 * Confirms both the acquirer and issuer side of a transaction are
 * registered, active SGQR+ participants before routing proceeds.
 */
@Service
public class ParticipantValidationService {

    private final ParticipantRepository participantRepository;

    public ParticipantValidationService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Participant validate(Long participantId, String role) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotFoundException(
                        role + " participant not found: id=" + participantId));

        if (participant.getStatus() != ParticipantStatus.ACTIVE) {
            throw new ParticipantNotActiveException(
                    role + " participant '" + participant.getName() + "' is " + participant.getStatus());
        }
        return participant;
    }
}
