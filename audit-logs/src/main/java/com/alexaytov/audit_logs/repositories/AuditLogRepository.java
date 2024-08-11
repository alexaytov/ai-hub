package com.alexaytov.audit_logs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.audit_logs.models.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
