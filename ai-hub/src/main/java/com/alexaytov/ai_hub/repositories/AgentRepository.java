package com.alexaytov.ai_hub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexaytov.ai_hub.model.entities.Agent;

public interface AgentRepository extends JpaRepository<Agent, Long> {
}
