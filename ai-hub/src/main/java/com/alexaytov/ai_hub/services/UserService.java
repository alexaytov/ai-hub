package com.alexaytov.ai_hub.services;

import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UpdateUserRequest;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.entities.User;

public interface UserService {
    User getUser();

    UserDto login(CredentialsDto credentialsDto);

    UserDto register(SignUpDto userDto);

    UserDto updateUser(UpdateUserRequest request);
}
