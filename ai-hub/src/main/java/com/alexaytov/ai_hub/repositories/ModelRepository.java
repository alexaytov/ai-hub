package com.alexaytov.ai_hub.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.ai_hub.model.entities.AIModel;

public interface ModelRepository extends JpaRepository<AIModel, Long> {

    Optional<AIModel> findByName(String name);
}
