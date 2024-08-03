package com.alexaytov.ai_hub.utils;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.dtos.SignUpDto;
import com.alexaytov.ai_hub.dtos.UserDto;
import com.alexaytov.ai_hub.entities.User;

// TODO User Model Mapper
@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(null, user.getUsername(), user.getPassword());
    }

    public User signUpToUser(SignUpDto userDto) {
        User user = new User();
        user.setUsername(userDto.username());
        user.setPassword(Arrays.toString(userDto.password()));
        return user;
    }
}
