package com.alexaytov.ai_hub.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.model.entities.MessageType;
import com.alexaytov.ai_hub.model.entities.MessageTypeEntity;
import com.alexaytov.ai_hub.repositories.MessageTypeRepository;

@Component
@Order(1)
public class InitMessageTypes implements CommandLineRunner {

    private final MessageTypeRepository repository;

    public InitMessageTypes(MessageTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            MessageTypeEntity admin = new MessageTypeEntity();
            admin.setType(MessageType.USER);

            MessageTypeEntity user = new MessageTypeEntity();
            user.setType(MessageType.ASSISTANT);

            repository.saveAll(List.of(admin, user));
        }
    }
}
