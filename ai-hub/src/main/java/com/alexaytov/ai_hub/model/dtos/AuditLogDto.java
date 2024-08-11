package com.alexaytov.ai_hub.model.dtos;

import java.util.Objects;

public class AuditLogDto {
    private String message;
    private Long timestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLogDto that = (AuditLogDto) o;
        return Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getTimestamp());
    }
}
