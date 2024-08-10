package com.alexaytov.ai_hub.services;

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

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class AuditLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);

    private final RestTemplate template;
    private final String endpoint;
    private final UserService userService;

    public AuditLogService(@Value("${audit-log.service.url}") String endpoint, RestTemplate template, UserService userService) {
        this.template = template;
        this.endpoint = endpoint;
        this.userService = userService;
    }

    public void createUser(Long id) {
        AuditLogUserDto body = new AuditLogUserDto(id);
        try {
            template.postForObject(endpoint, body, AuditLogDto.class);
        } catch (RestClientException ex) {
            LOGGER.error("Error while creating user in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while creating user in audit log service");
        }
    }

    public void deleteUser() {
        Long id = userService.getUser().getId();
        try {
            template.delete(endpoint + "/users/" + id);
        } catch (RestClientException ex) {
            LOGGER.error("Error while deleting user in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while deleting user in audit log service");
        }
    }

    public void postAuditLog(String message) {
        Long userId = userService.getUser().getId();
        AuditLogDto body = new AuditLogDto();
        body.setMessage(message);
        try {
            template.put(endpoint + "/users/" + userId + "/logs", body);
        } catch (RestClientException ex) {
            LOGGER.error("Error while posting audit log in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while posting audit log in audit log service");
        }
    }

    public List<AuditLogDto> getAuditLogs() {
        Long userId = userService.getUser().getId();
        try {
            AuditLogDto[] logs = template.getForObject(endpoint + "/users/" + userId + "/logs", AuditLogDto[].class);
            return List.of(logs);
        } catch (RestClientException ex) {
            LOGGER.error("Error while getting audit logs in audit log service", ex);
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error while getting audit logs in audit log service");
        }
    }


}
