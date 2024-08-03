package com.alexaytov.ai_hub.services;

import java.nio.CharBuffer;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alexaytov.ai_hub.dtos.CredentialsDto;
import com.alexaytov.ai_hub.dtos.SignUpDto;
import com.alexaytov.ai_hub.dtos.UserDto;
import com.alexaytov.ai_hub.entities.User;
import com.alexaytov.ai_hub.exceptions.UserException;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.utils.UserMapper;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByUsername(credentialsDto.login())
            .orElseThrow(() -> new UserException("Unknown user"));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new UserException("Invalid password");
    }

    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userDto.username());

        if (optionalUser.isPresent()) {
            throw new UserException("Login already exists");
        }

        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserException("Unknown user"));
        return userMapper.toUserDto(user);
    }
}
