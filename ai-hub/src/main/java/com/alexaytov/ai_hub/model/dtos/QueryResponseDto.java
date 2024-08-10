package com.alexaytov.ai_hub.model.dtos;

public class QueryResponseDto {

    private String content;
    private String role;

    public QueryResponseDto() {
        role = "assistant";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
