package com.alexaytov.audit_logs.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.audit_logs.models.dtos.AuditLogDto;
import com.alexaytov.audit_logs.models.dtos.UserDto;
import com.alexaytov.audit_logs.models.entities.AuditLog;
import com.alexaytov.audit_logs.models.entities.User;
import com.alexaytov.audit_logs.repositories.AuditLogRepository;
import com.alexaytov.audit_logs.repositories.UserRepository;

import jakarta.transaction.Transactional;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class UserServiceImpl implements com.alexaytov.audit_logs.services.UserService {

    private final UserRepository repository;
    private final AuditLogRepository logsRepository;

    public UserServiceImpl(UserRepository repository, AuditLogRepository logsRepository) {
        this.repository = repository;
        this.logsRepository = logsRepository;
    }

    @Override
    public void createUser(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getId());
        repository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        User user = repository.findByUserId(id)
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "User not found"));
        user.setDeleted(true);
        repository.save(user);
    }

    @Override
    @Transactional
    public void addAuditLog(long userId, AuditLogDto dto) {
        User user = repository.findByUserId(userId)
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "User not found"));

        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setMessage(dto.getMessage());
        logsRepository.save(log);

        user.getAuditLogs().add(log);
        repository.save(user);
    }

    @Override
    public List<AuditLogDto> getUserLogs(Long id) {
        User user = repository.findByUserId(id)
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "User not found"));
        return user.getAuditLogs().stream()
            .map(a -> {
                AuditLogDto dto = new AuditLogDto();
                dto.setMessage(a.getMessage());
                dto.setTimestamp(a.getTimestamp());
                return dto;
            }).toList();
    }
}
