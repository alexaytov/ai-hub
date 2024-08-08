package com.alexaytov.ai_hub.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;

@Component
@Order(1)
public class InitUserRoles implements CommandLineRunner {

    private final UserRoleRepository repository;

    public InitUserRoles(UserRoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            UserRoleEntity admin = new UserRoleEntity();
            admin.setRole(UserRole.ADMIN);

            UserRoleEntity user = new UserRoleEntity();
            user.setRole(UserRole.USER);

            repository.saveAll(List.of(admin, user));
        }
    }
}
