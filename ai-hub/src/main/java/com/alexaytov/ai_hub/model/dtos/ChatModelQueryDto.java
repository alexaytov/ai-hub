package com.alexaytov.ai_hub.model.dtos;

import jakarta.validation.constraints.NotNull;

public class ChatModelQueryDto {

    @NotNull
    private String content;

    public @NotNull String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }
}
