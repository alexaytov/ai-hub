package com.alexaytov.audit_logs.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alexaytov.audit_logs.models.entities.User;
import com.alexaytov.audit_logs.repositories.UserRepository;

@Component
public class InitAdminUser implements CommandLineRunner {

    private final UserRepository userRepository;

    public InitAdminUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUserId(1L);
            userRepository.save(user);
        }
    }
}
