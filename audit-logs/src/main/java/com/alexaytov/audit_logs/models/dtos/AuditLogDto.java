package com.alexaytov.audit_logs.models.dtos;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public class AuditLogDto {

    @NotNull
    @Length(max = 255)
    private String message;

    private Long timestamp;

    public @NotNull @Length(max = 255) String getMessage() {
        return message;
    }

    public void setMessage(@NotNull @Length(max = 255) String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
