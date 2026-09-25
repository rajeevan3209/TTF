package com.sgqrplus.switchengine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scheme")
public class Scheme {

    @Id
    @Column(name = "code", nullable = false, updatable = false, length = 30)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "qr_identifier_pattern", nullable = false)
    private String qrIdentifierPattern;

    @Column(name = "message_format", nullable = false)
    private String messageFormat;

    protected Scheme() {
    }

    public Scheme(String code, String name, String qrIdentifierPattern, String messageFormat) {
        this.code = code;
        this.name = name;
        this.qrIdentifierPattern = qrIdentifierPattern;
        this.messageFormat = messageFormat;
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
