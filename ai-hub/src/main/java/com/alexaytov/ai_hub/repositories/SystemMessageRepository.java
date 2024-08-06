package com.alexaytov.ai_hub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.ai_hub.model.entities.SystemMessage;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, Long> {

}
