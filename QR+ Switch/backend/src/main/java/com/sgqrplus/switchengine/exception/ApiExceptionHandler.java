package com.sgqrplus.switchengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SchemeNotRecognizedException.class)
    public ResponseEntity<Object> handleSchemeNotRecognized(SchemeNotRecognizedException ex) {
        return body(HttpStatus.BAD_REQUEST, "SCHEME_NOT_RECOGNIZED", ex.getMessage());
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ResponseEntity<Object> handleParticipantNotFound(ParticipantNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, "PARTICIPANT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ParticipantNotActiveException.class)
    public ResponseEntity<Object> handleParticipantNotActive(ParticipantNotActiveException ex) {
        return body(HttpStatus.CONFLICT, "PARTICIPANT_NOT_ACTIVE", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    private ResponseEntity<Object> body(HttpStatus status, String code, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("status", status.value());
        payload.put("errorCode", code);
        payload.put("message", message);
        return ResponseEntity.status(status).body(payload);
    }
}
