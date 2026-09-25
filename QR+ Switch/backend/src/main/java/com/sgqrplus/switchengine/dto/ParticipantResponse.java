package com.sgqrplus.switchengine.dto;

import com.sgqrplus.switchengine.domain.Participant;

public class ParticipantResponse {

    private Long id;
    private String name;
    private String schemeCode;
    private String schemeName;
    private String participantType;
    private String status;

    public static ParticipantResponse from(Participant p) {
        ParticipantResponse r = new ParticipantResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.schemeCode = p.getScheme().getCode();
        r.schemeName = p.getScheme().getName();
        r.participantType = p.getParticipantType().name();
        r.status = p.getStatus().name();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public String getParticipantType() {
        return participantType;
    }

    public String getStatus() {
        return status;
    }
}
