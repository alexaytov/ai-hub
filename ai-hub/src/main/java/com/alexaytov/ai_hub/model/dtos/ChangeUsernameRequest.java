package com.alexaytov.ai_hub.model.dtos;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public class ChangeUsernameRequest {

    @NotNull
    @Length(max = 40)
    private String username;

    public @NotNull @Length(max = 40) String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @Length(max = 40) String username) {
        this.username = username;
    }
}
