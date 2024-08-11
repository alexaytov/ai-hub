package com.alexaytov.audit_logs.models.dtos;

import jakarta.validation.constraints.NotNull;

public class UserDto {

    @NotNull
    private Long id;

    public @NotNull Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }
}
