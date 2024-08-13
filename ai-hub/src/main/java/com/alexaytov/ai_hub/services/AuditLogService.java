package com.alexaytov.ai_hub.services;

import java.util.List;

import com.alexaytov.ai_hub.model.dtos.AuditLogDto;

public interface AuditLogService {
    void createUser(Long id);

    void deleteUser(Long id);

    void postAuditLog(String message);

    List<AuditLogDto> getAuditLogs();
}
