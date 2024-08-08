package com.alexaytov.ai_hub.services;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.exceptions.UserException;
import com.alexaytov.ai_hub.model.dtos.UpdateUserRequest;
import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;
import com.alexaytov.ai_hub.utils.UserMapper;

import jakarta.transaction.Transactional;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User getUser() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(details.getUsername()).orElseThrow(() -> new UserException("User not found"));
    }

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByUsername(credentialsDto.username())
            .orElseThrow(() -> new UserException("Unknown user"));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new UserException("Invalid password");
    }

    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userDto.username());

        if (optionalUser.isPresent()) {
            throw new UserException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));

        UserRoleEntity userRole = userRoleRepository.findByRole(UserRole.USER);
        user.setRoles(List.of(userRole));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserException("Unknown user"));
        return userMapper.toUserDto(user);
    }

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
        return userMapper.toUserDto(user);
    }
}
