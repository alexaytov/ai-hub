package com.alexaytov.ai_hub.init;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.model.entities.AIModelType;
import com.alexaytov.ai_hub.model.entities.AIModelTypeEntity;
import com.alexaytov.ai_hub.repositories.ModelTypeRepository;

@Component
public class InitChatModelTypes implements CommandLineRunner {

    private final ModelTypeRepository repository;

    public InitChatModelTypes(ModelTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.findByType(AIModelType.OPEN_AI).isEmpty()) {
            AIModelTypeEntity entity = new AIModelTypeEntity();
            entity.setType(AIModelType.OPEN_AI);
            repository.save(entity);
        }

        if (repository.findByType(AIModelType.AI_CORE).isEmpty()) {
            AIModelTypeEntity entity = new AIModelTypeEntity();
            entity.setType(AIModelType.AI_CORE);
            repository.save(entity);
        }
    }
}
