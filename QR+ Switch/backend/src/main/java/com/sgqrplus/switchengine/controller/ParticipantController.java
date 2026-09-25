package com.sgqrplus.switchengine.controller;

import com.sgqrplus.switchengine.domain.Participant;
import com.sgqrplus.switchengine.domain.ParticipantStatus;
import com.sgqrplus.switchengine.domain.ParticipantType;
import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.dto.ParticipantRequest;
import com.sgqrplus.switchengine.dto.ParticipantResponse;
import com.sgqrplus.switchengine.repository.ParticipantRepository;
import com.sgqrplus.switchengine.repository.SchemeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantRepository participantRepository;
    private final SchemeRepository schemeRepository;

    public ParticipantController(ParticipantRepository participantRepository, SchemeRepository schemeRepository) {
        this.participantRepository = participantRepository;
        this.schemeRepository = schemeRepository;
    }

    @GetMapping
    public List<ParticipantResponse> listParticipants() {
        return participantRepository.findAll().stream()
                .map(ParticipantResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantResponse createParticipant(@Valid @RequestBody ParticipantRequest request) {
        Scheme scheme = schemeRepository.findById(request.getSchemeCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unknown schemeCode: " + request.getSchemeCode()));

        ParticipantType type;
        try {
            type = ParticipantType.valueOf(request.getParticipantType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid participantType: " + request.getParticipantType());
        }

        Participant participant = new Participant(request.getName(), scheme, type, ParticipantStatus.ACTIVE);
        return ParticipantResponse.from(participantRepository.save(participant));
    }
}
