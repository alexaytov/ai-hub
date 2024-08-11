package com.alexaytov.audit_logs.services;

import java.util.List;

import com.alexaytov.audit_logs.models.dtos.AuditLogDto;
import com.alexaytov.audit_logs.models.dtos.UserDto;

import jakarta.transaction.Transactional;

public interface UserService {
    void createUser(UserDto dto);

    void deleteUser(long id);

    void addAuditLog(long userId, AuditLogDto dto);

    List<AuditLogDto> getUserLogs(Long id);
}
