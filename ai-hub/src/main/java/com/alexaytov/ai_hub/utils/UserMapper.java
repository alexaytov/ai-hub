package com.alexaytov.ai_hub.utils;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.entities.UserRoleEntity;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles().stream().map(UserRoleEntity::getRole).toList());
        return dto;
    }

    public User signUpToUser(SignUpDto userDto) {
        User user = new User();
        user.setUsername(userDto.username());
        return user;
    }
}
