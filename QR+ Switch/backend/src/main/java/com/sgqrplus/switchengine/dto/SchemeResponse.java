package com.sgqrplus.switchengine.dto;

import com.sgqrplus.switchengine.domain.Scheme;

public class SchemeResponse {

    private String code;
    private String name;
    private String qrIdentifierPattern;
    private String messageFormat;

    public static SchemeResponse from(Scheme s) {
        SchemeResponse r = new SchemeResponse();
        r.code = s.getCode();
        r.name = s.getName();
        r.qrIdentifierPattern = s.getQrIdentifierPattern();
        r.messageFormat = s.getMessageFormat();
        return r;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getQrIdentifierPattern() {
        return qrIdentifierPattern;
    }

    public String getMessageFormat() {
        return messageFormat;
    }
}
