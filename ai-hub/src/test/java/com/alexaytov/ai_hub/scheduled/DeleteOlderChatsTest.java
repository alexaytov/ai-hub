package com.alexaytov.ai_hub.scheduled;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexaytov.ai_hub.services.ChatService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DeleteOlderChatsTest {

    private ChatService chatService;
    private DeleteOlderChats classUnderTest;

    @BeforeEach
    void setUp() {
        chatService = mock(ChatService.class);
        classUnderTest = new DeleteOlderChats(chatService);
    }

    @Test
    void whenExecuting_thenCorrectChatsAreDeleted() {
        classUnderTest.deleteOlderChats();
        verify(chatService).deleteOlderThan(TimeUnit.DAYS.toMillis(30));
    }

}