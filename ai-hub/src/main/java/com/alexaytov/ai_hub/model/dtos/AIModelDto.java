package com.alexaytov.ai_hub.model.dtos;

import org.hibernate.validator.constraints.Length;

import com.alexaytov.ai_hub.model.entities.AIModelType;

import jakarta.validation.constraints.NotNull;

public class AIModelDto {

    private Long id;

    @NotNull
    @Length(min = 1, max = 50)
    private String name;

    @NotNull
    @Length(min = 1, max = 255)
    private String description;

    @NotNull
    AIModelType type;

    @NotNull
    String apiKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @Length(min = 1, max = 50) String getName() {
        return name;
    }

    public void setName(@NotNull @Length(min = 1, max = 50) String name) {
        this.name = name;
    }

    public @NotNull @Length(min = 1, max = 255) String getDescription() {
        return description;
    }

    public void setDescription(@NotNull @Length(min = 1, max = 255) String description) {
        this.description = description;
    }

    public @NotNull AIModelType getType() {
        return type;
    }

    public void setType(@NotNull AIModelType type) {
        this.type = type;
    }

    public @NotNull String getApiKey() {
        return apiKey;
    }

    public void setApiKey(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    public void clearApiKey() {
        this.apiKey = null;
    }
}
