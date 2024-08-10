package com.alexaytov.ai_hub.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.ChatModelQueryDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
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
import dev.langchain4j.model.output.Response;
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

    public String query(Long chatId, ChatModelQueryDto query) {
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

        Response<AiMessage> response = model.generate(messages);

        com.alexaytov.ai_hub.model.entities.ChatMessage newMessage = new com.alexaytov.ai_hub.model.entities.ChatMessage();
        newMessage.setContent(response.content().text());
        newMessage.setType(typeRepository.findByType(MessageType.ASSISTANT));

        chat.getMessages().add(newMessage);
        chatRepository.save(chat);

        return response.content().text();
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
}
