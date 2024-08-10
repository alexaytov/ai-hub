package com.alexaytov.ai_hub.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.ChatMessageDto;
import com.alexaytov.ai_hub.model.dtos.ChatModelQueryDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.model.dtos.GetChatResponse;
import com.alexaytov.ai_hub.model.dtos.QueryResponseDto;
import com.alexaytov.ai_hub.model.entities.Agent;
import com.alexaytov.ai_hub.model.entities.Chat;
import com.alexaytov.ai_hub.model.entities.MessageType;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.AgentRepository;
import com.alexaytov.ai_hub.repositories.ChatRepository;
import com.alexaytov.ai_hub.repositories.MessageTypeRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.utils.AES256Encryption;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.transaction.Transactional;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ChatService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ChatRepository repository;
    private final AgentRepository agentRepository;
    private final ModelRepository modelRepository;
    private final ChatRepository chatRepository;
    private final AES256Encryption encryption;
    private final MessageTypeRepository typeRepository;

    public ChatService(UserRepository userRepository, UserService userService, ChatRepository repository, AgentRepository agentRepository, ModelRepository modelRepository, ChatRepository chatRepository, AES256Encryption encryption, MessageTypeRepository typeRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.repository = repository;
        this.agentRepository = agentRepository;
        this.modelRepository = modelRepository;
        this.chatRepository = chatRepository;
        this.encryption = encryption;
        this.typeRepository = typeRepository;
    }

    public QueryResponseDto query(Long chatId, ChatModelQueryDto query) {
        User user = userService.getUser();
        Chat chat = chatRepository.findById(chatId)
            .filter(c -> c.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Chat not found"));

        String apiKey = encryption.decrypt(chat.getModel().getApiKey());
        OpenAiChatModel model = OpenAiChatModel.withApiKey(apiKey);

        List<ChatMessage> messages = chat.getMessages().stream()
            .flatMap(msg -> {
                if (msg.getType().getType() == MessageType.USER) {
                    return Stream.of(new UserMessage(msg.getContent()));
                }

                if (msg.getType().getType() == MessageType.ASSISTANT) {
                    return Stream.of(new AiMessage(msg.getContent()));
                }
                return Stream.empty();
            })
            .toList();

        int size = messages.size();
        messages = messages.stream()
            .skip(size > 9 ? size - 9 : 0)
            .limit(9)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        messages.add(new UserMessage(query.getContent()));


        String response;
        try {
            response = model.generate(messages).content().text();
        } catch (Exception ex) {
            response = "An error occurred while processing the request. Please try again later.";
        }

        com.alexaytov.ai_hub.model.entities.ChatMessage newMessage = new com.alexaytov.ai_hub.model.entities.ChatMessage();
        newMessage.setContent(response);
        newMessage.setType(typeRepository.findByType(MessageType.ASSISTANT));

        chat.getMessages().add(newMessage);
        chatRepository.save(chat);

        QueryResponseDto responseDto = new QueryResponseDto();
        responseDto.setRole("assistant");
        responseDto.setContent(response);

        return responseDto;
    }

    @Transactional
    public ChatDto createChat(CreateChatDto dto) {
        User user = userService.getUser();
        if (dto.getAgentId() != null) {
            Agent agent = agentRepository.findById(dto.getAgentId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Agent not found"));

            Chat chat = new Chat();
            chat.setModel(agent.getModel());
            chat.setUser(user);
            repository.save(chat);

            user.getChats().add(chat);
            userRepository.save(user);

            ChatDto created = new ChatDto();
            created.setId(chat.getId());
            created.setAgentId(dto.getAgentId());
            created.setModelId(agent.getModel().getId());
            return created;
        }

        if (dto.getModelId() != null) {
            var model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Model not found"));

            Chat chat = new Chat();
            chat.setModel(model);
            chat.setUser(user);
            repository.save(chat);

            user.getChats().add(chat);
            userRepository.save(user);

            ChatDto created = new ChatDto();
            created.setId(chat.getId());
            created.setModelId(dto.getModelId());
            return created;
        }

        throw new HttpClientErrorException(BAD_REQUEST, "Either agentId or modelId must be provided");
    }

    @Transactional
    public void deleteChat(Long id) {
        User user = userService.getUser();

        Optional<Chat> chat = repository.findById(id)
            .filter(c -> c.getModel().getUser().getId().equals(user.getId()));

        if (chat.isEmpty()) {
            return;
        }

        chat.get().setUser(null);
        repository.save(chat.get());
        repository.deleteById(chat.get().getId());
    }

    public GetChatResponse getChat(Long id) {
        User user = userService.getUser();
        Chat chat = user.getChats().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Chat not found"));

        List<ChatMessageDto> messages = chat.getMessages().stream()
            .map(msg -> {
                ChatMessageDto dto = new ChatMessageDto();
                dto.setType(msg.getType().getType());
                dto.setContent(msg.getContent());
                return dto;
            }).toList();
        GetChatResponse response = new GetChatResponse();
        response.setMessages(messages);
        return response;
    }

    public List<ChatDto> getChats() {
        return repository.findAll().stream()
            .filter(chat -> chat.getUser().getId().equals(userService.getUser().getId()))
            .map(c -> {
                ChatDto dto = new ChatDto();
                dto.setId(c.getId());
                if (c.getAgent() != null) {
                    dto.setAgentId(c.getAgent().getId());
                    dto.setModelId(c.getAgent().getModel().getId());
                }

                if (c.getModel() != null) {
                    dto.setModelId(c.getModel().getId());
                }
                dto.setCreated(c.getCreated());

                return dto;
            }).toList();
    }

    @Transactional
    public void deleteOlderThan(long milliseconds) {
        chatRepository.findAll().stream()
            .filter(chat -> System.currentTimeMillis() - chat.getCreated() < milliseconds)
            .forEach(chat -> {
                chat.setUser(null);
                chatRepository.save(chat);
                chatRepository.delete(chat);
            });
    }
}
