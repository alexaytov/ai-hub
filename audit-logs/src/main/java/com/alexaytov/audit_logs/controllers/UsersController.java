package com.alexaytov.audit_logs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.audit_logs.models.dtos.AuditLogDto;
import com.alexaytov.audit_logs.models.dtos.UserDto;
import com.alexaytov.audit_logs.services.UserService;

import jakarta.validation.Valid;

@RestController
public class UsersController {

    private final UserService service;

    public UsersController(UserService service) {
        this.service = service;
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        service.createUser(dto);
        return ResponseEntity.status(201).body(dto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/logs")
    public ResponseEntity<Void> addAuditLog(@PathVariable Long id, @Valid @RequestBody AuditLogDto dto) {
        service.addAuditLog(id, dto);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/users/{id}/logs")
    public ResponseEntity<List<AuditLogDto>> getUserLogs(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserLogs(id));
    }
}
