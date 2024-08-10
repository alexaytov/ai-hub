package com.alexaytov.ai_hub.scheduled;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.repositories.ChatRepository;
import com.alexaytov.ai_hub.services.ChatService;

@Component
public class DeleteOlderChats {

    private final ChatService service;

    public DeleteOlderChats(ChatService service) {
        this.service = service;
    }

    // Schedule Once every Day at 00:00
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOlderChats() {
        // Get all Chats older than 30 days
        service.deleteOlderThan(TimeUnit.DAYS.toMillis(30));
    }
}
