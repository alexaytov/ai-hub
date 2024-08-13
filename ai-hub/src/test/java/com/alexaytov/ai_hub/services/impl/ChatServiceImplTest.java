package com.alexaytov.ai_hub.services.impl;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.ChatMessageDto;
import com.alexaytov.ai_hub.model.dtos.ChatModelQueryDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.model.dtos.GetChatResponse;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.model.entities.Agent;
import com.alexaytov.ai_hub.model.entities.Chat;
import com.alexaytov.ai_hub.model.entities.ChatMessage;
import com.alexaytov.ai_hub.model.entities.MessageTypeEntity;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.enums.MessageType;
import com.alexaytov.ai_hub.repositories.AgentRepository;
import com.alexaytov.ai_hub.repositories.ChatMessageRepository;
import com.alexaytov.ai_hub.repositories.ChatRepository;
import com.alexaytov.ai_hub.repositories.MessageTypeRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.services.AIService;
import com.alexaytov.ai_hub.services.UserService;
import com.alexaytov.ai_hub.utils.Encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatServiceImplTest {

    private UserService userService;
    private ChatRepository chatRepository;
    private AgentRepository agentRepository;
    private ModelRepository modelRepository;
    private Encryption encryption;
    private MessageTypeRepository typeRepository;
    private ChatMessageRepository messageRepository;
    private AIService aiService;


    private ChatServiceImpl classUnderTest;

    @BeforeEach
    void setUp() {
        messageRepository = mock(ChatMessageRepository.class);
        userService = mock(UserService.class);
        chatRepository = mock(ChatRepository.class);
        agentRepository = mock(AgentRepository.class);
        modelRepository = mock(ModelRepository.class);
        encryption = mock(Encryption.class);
        typeRepository = mock(MessageTypeRepository.class);
        aiService = mock(AIService.class);

        classUnderTest = new ChatServiceImpl(
            userService,
            chatRepository,
            agentRepository,
            modelRepository,
            chatRepository,
            encryption,
            typeRepository,
            messageRepository,
            aiService
        );
    }

    @Test
    void whenGettingChats_thenCorrectValuesAreReturned() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        Chat chat = new Chat();
        chat.setId(1L);

        AIModel model = new AIModel();
        model.setId(1L);

        Agent agent = new Agent();
        agent.setId(1L);
        agent.setModel(model);
        chat.setAgent(agent);
        chat.setModel(model);
        user.setChats(List.of(chat));

        List<ChatDto> chats = classUnderTest.getChats();

        assertEquals(1, chats.size());
        ChatDto actual = chats.get(0);

        assertEquals(chat.getId(), actual.getId());
        assertEquals(model.getId(), actual.getModelId());
        assertEquals(agent.getId(), actual.getAgentId());
    }

    @Test
    void givenExistingChat_whenGettingChat_thenCorrectValueIsReturned() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        AIModel model = new AIModel();
        model.setId(1L);

        Chat chat = new Chat();
        chat.setModel(model);
        user.setChats(List.of(chat));

        chat.setId(1L);
        ChatMessage msg = new ChatMessage();
        MessageTypeEntity type = new MessageTypeEntity();
        type.setType(MessageType.USER);
        msg.setContent("content");
        msg.setType(type);
        chat.setMessages(List.of(msg));

        GetChatResponse actual = classUnderTest.getChat(1L);

        assertEquals(1, actual.getMessages().size());
        ChatMessageDto actualMsg = actual.getMessages().get(0);
        assertEquals("content", actualMsg.getContent());
        assertEquals(MessageType.USER, actualMsg.getType());
    }

    @Test
    void givenNonExistentChat_whenGettingChat_thenCorrectExceptionIsThrown() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        Chat chat = new Chat();
        user.setChats(List.of(chat));

        chat.setId(1L);
        ChatMessage msg = new ChatMessage();
        MessageTypeEntity type = new MessageTypeEntity();
        type.setType(MessageType.USER);
        msg.setContent("content");
        msg.setType(type);
        chat.setMessages(List.of(msg));

        try {
            classUnderTest.getChat(2L);
        } catch (Exception e) {
            assertEquals("400 Chat not found", e.getMessage());
        }
    }

    @Test
    void givenNeitherAgentOrModel_whenCreatingChat_thenCorrectExceptionIsThrown() {
        CreateChatDto dto = new CreateChatDto();

        try {
            classUnderTest.createChat(dto);
        } catch (Exception e) {
            assertEquals("400 Either agentId or modelId must be provided", e.getMessage());
        }
    }

    @Test
    void givenExistingChat_whenDeletingChat_thenCorrectChatIsDeleted() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        Chat chat = new Chat();
        chat.setUser(user);
        user.setChats(List.of(chat));
        when(chatRepository.findById(1L)).thenReturn(java.util.Optional.of(chat));

        chat.setId(1L);

        classUnderTest.deleteChat(1L);

        verify(chatRepository).deleteById(1L);
    }

    @Test
    void givenNonExistingChat_whenDeleting_thenNothingHappens() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        Chat chat = new Chat();
        chat.setUser(user);
        user.setChats(List.of(chat));
        when(chatRepository.findById(1L)).thenReturn(java.util.Optional.of(chat));

        chat.setId(1L);

        classUnderTest.deleteChat(2L);

        verify(chatRepository, never()).deleteById(1L);
    }

    @Test
    void whenDeletingOlderThan_thenCorrectChatsAreDeleted() {
        long now = 1000L;
        classUnderTest = new ChatServiceImpl(
            userService,
            chatRepository,
            agentRepository,
            modelRepository,
            chatRepository,
            encryption,
            typeRepository,
            messageRepository,
            () -> now,
            aiService
        );

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setCreated(now - 1000);
        Chat chat2 = new Chat();
        chat2.setId(2L);
        chat2.setCreated(now - 2000);
        Chat chat3 = new Chat();
        chat3.setId(3L);
        chat3.setCreated(now - 3000);
        when(chatRepository.findAll()).thenReturn(List.of(chat, chat2, chat3));

        classUnderTest.deleteOlderThan(2000);

        verify(chatRepository).deleteById(chat.getId());
        verify(chatRepository, never()).deleteById(chat2.getId());
        verify(chatRepository, never()).deleteById(chat3.getId());
    }

    @Test
    void givenModel_whenCreatingChat_thenCorrectChatIsCreated() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        AIModel model = new AIModel();
        model.setId(1L);
        when(modelRepository.findById(1L)).thenReturn(java.util.Optional.of(model));

        CreateChatDto dto = new CreateChatDto();
        dto.setModelId(1L);

        classUnderTest.createChat(dto);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatCaptor.capture());
        verify(userService).save(user);

        assertEquals(1L, chatCaptor.getValue().getModel().getId());
    }

    @Test
    void givenAgent_whenCreatingChat_thenCorrectChatIsCreated() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        AIModel model = new AIModel();
        model.setId(1L);
        when(modelRepository.findById(1L)).thenReturn(java.util.Optional.of(model));

        Agent agent = new Agent();
        agent.setId(1L);
        agent.setUser(user);
        agent.setModel(model);
        when(agentRepository.findById(1L)).thenReturn(Optional.of(agent));

        CreateChatDto dto = new CreateChatDto();
        dto.setAgentId(1L);

        classUnderTest.createChat(dto);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatCaptor.capture());
        verify(userService).save(user);

        assertEquals(1L, chatCaptor.getValue().getModel().getId());
        assertEquals(1L, chatCaptor.getValue().getAgent().getId());
    }

    @Test
    void givenNonExistentModel_whenCreatingChat_thenExceptionIsThrown() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        AIModel model = new AIModel();
        model.setId(1L);
        when(modelRepository.findById(1L)).thenReturn(java.util.Optional.of(model));

        CreateChatDto dto = new CreateChatDto();
        dto.setModelId(2L);

        try {
            classUnderTest.createChat(dto);
        } catch (Exception e) {
            assertEquals("400 Model not found", e.getMessage());
        }
    }

    @Test
    void givenNonExistentChat_whenQuerying_thenCorrectResponseIsReturned() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        ChatModelQueryDto dto = new ChatModelQueryDto();
        dto.setContent("Hello");

        assertThrows(HttpClientErrorException.class, () -> classUnderTest.query(1L, dto));
    }
}