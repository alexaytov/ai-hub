package com.alexaytov.ai_hub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.ai_hub.model.entities.MessageType;
import com.alexaytov.ai_hub.model.entities.MessageTypeEntity;

public interface MessageTypeRepository extends JpaRepository<MessageTypeEntity, Long> {

    MessageTypeEntity findByType(MessageType type);
}
