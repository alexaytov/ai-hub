package com.alexaytov.ai_hub.services.impl;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UpdateUserRequest;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;
import com.alexaytov.ai_hub.services.UserService;

import jakarta.transaction.Transactional;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class UserServiceImpl implements UserService {

    private static final String UNKNOWN_USER = "Unknown user";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUser() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(details.getUsername()).orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, UNKNOWN_USER));
    }

    @Override
    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByUsername(credentialsDto.username())
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, UNKNOWN_USER));

        if (passwordEncoder.matches(credentialsDto.password(), user.getPassword())) {
            return toUserDto(user);
        }

        throw new HttpClientErrorException(BAD_REQUEST, UNKNOWN_USER);
    }

    @Override
    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userDto.username());

        if (optionalUser.isPresent()) {
            throw new HttpClientErrorException(BAD_REQUEST, "Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));

        UserRoleEntity userRole = userRoleRepository.findByRole(UserRole.USER);
        user.setRoles(List.of(userRole));

        User savedUser = userRepository.save(user);

        return toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserRequest request) {
        if (request.getUsername() != null && userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new HttpClientErrorException(BAD_REQUEST, "Username already exists");
        }

        User user = getUser();
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(CharBuffer.wrap(request.getPassword())));
        }

        userRepository.save(user);
        return toUserDto(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser() {
        User user = getUser();
        if (user.getId().equals(1L)) {
            throw new HttpClientErrorException(BAD_REQUEST, "Cannot delete the admin user");
        }

        userRepository.deleteById(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles().stream().map(UserRoleEntity::getRole).toList());
        return dto;
    }
}
