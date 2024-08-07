package com.alexaytov.ai_hub.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.ai_hub.model.entities.AIModelType;
import com.alexaytov.ai_hub.model.entities.AIModelTypeEntity;

public interface ModelTypeRepository extends JpaRepository<AIModelTypeEntity, Long> {

    Optional<AIModelTypeEntity> findByType(AIModelType type);
}
