package com.alexaytov.ai_hub.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;

import jakarta.transaction.Transactional;

@Component
@Order(3)
public class InitAdminUser implements CommandLineRunner {

    public static final String ADMIN = "admin";
    private final UserRepository repository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public InitAdminUser(UserRepository repository, UserRoleRepository roleRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.findByUsername(ADMIN).isEmpty()) {
            User user = new User();
            user.setUsername(ADMIN);
            user.setPassword(encoder.encode(ADMIN));

            UserRoleEntity adminRole = roleRepository.findByRole(UserRole.ADMIN);
            UserRoleEntity userRole = roleRepository.findByRole(UserRole.USER);
            List<UserRoleEntity> roles = List.of(adminRole, userRole);

            user.setRoles(roles);
            repository.save(user);
        }
    }
}
