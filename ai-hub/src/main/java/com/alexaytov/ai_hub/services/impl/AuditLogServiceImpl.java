package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alexaytov.ai_hub.model.dtos.AuditLogDto;
import com.alexaytov.ai_hub.model.dtos.AuditLogUserDto;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.UserService;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final RestTemplate template;
    private final String endpoint;
    private final UserService userService;

    public AuditLogServiceImpl(@Value("${audit-log.service.url}") String endpoint, RestTemplate template, UserService userService) {
        this.template = template;
        this.endpoint = endpoint;
        this.userService = userService;
    }

    @Override
    public void createUser(Long id) {
        AuditLogUserDto body = new AuditLogUserDto(id);
        try {
            template.postForObject(endpoint + "/users", body, AuditLogDto.class);
        } catch (RestClientException ex) {
            LOGGER.error("Error while creating user in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while creating user in audit log service");
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            template.delete(format("%s/users/%d", endpoint, id));
        } catch (RestClientException ex) {
            LOGGER.error("Error while deleting user in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while deleting user in audit log service");
        }
    }

    @Override
    public void postAuditLog(String message) {
        Long userId = userService.getUser().getId();
        AuditLogDto body = new AuditLogDto();
        body.setMessage(message);
        try {
            template.put(format("%s/users/%s/logs", endpoint, userId), body);
        } catch (RestClientException ex) {
            LOGGER.error("Error while posting audit log in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while posting audit log in audit log service");
        }
    }

    @Override
    public List<AuditLogDto> getAuditLogs() {
        Long userId = userService.getUser().getId();
        try {
            AuditLogDto[] logs = template.getForObject(format("%s/users/%d/logs", endpoint, userId), AuditLogDto[].class);
            if (logs == null) {
                return List.of();
            }

            return List.of(logs);
        } catch (RestClientException ex) {
            LOGGER.error("Error while getting audit logs in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while getting audit logs in audit log service");
        }
    }


}
