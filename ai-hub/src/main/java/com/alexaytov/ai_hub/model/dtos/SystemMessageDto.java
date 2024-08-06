package com.alexaytov.ai_hub.model.dtos;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public class SystemMessageDto {

    private Long id;

    @NotNull
    @Length(max = 300)
    private String message;

    public SystemMessageDto() {
    }

    public SystemMessageDto(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
