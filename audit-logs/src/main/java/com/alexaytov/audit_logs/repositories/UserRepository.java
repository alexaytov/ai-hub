package com.alexaytov.audit_logs.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.audit_logs.models.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(Long id);
}
