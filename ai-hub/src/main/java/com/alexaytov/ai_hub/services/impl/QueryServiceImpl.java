package com.alexaytov.ai_hub.services.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.alexaytov.ai_hub.model.dtos.ChatMessageDto;
import com.alexaytov.ai_hub.model.dtos.QueryRequestDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.model.entities.DataSource;
import com.alexaytov.ai_hub.model.enums.MessageType;
import com.alexaytov.ai_hub.repositories.DataSourceRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.services.AIService;
import com.alexaytov.ai_hub.services.QueryService;
import com.alexaytov.ai_hub.services.UserService;
import com.alexaytov.ai_hub.utils.Encryption;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class QueryServiceImpl implements QueryService {

  private final UserService userService;
  private final Encryption encryption;
  private final ModelRepository modelRepository;
  private final AIService aiService;
  private final DataSourceRepository dataSourceRepository;

  public QueryServiceImpl(UserService userService, Encryption encryption,
      ModelRepository modelRepository,
      AIService aiService, DataSourceRepository dataSourceRepository) {
    this.userService = userService;
    this.encryption = encryption;
    this.modelRepository = modelRepository;
    this.aiService = aiService;
    this.dataSourceRepository = dataSourceRepository;
  }

  @Override
  public ChatMessageDto query(QueryRequestDto request) {
    AIModel model = modelRepository.findById(request.getModelId())
        .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Model not found"));
    String apiKey = encryption.decrypt(model.getApiKey());

    Map<String, String> defaultParameters = model.getParameters();
    defaultParameters.putAll(request.getCustomParameters());

    ChatLanguageModel languageModel = aiService.getModel(model.getType().getType(), apiKey,
        defaultParameters);
    List<ChatMessage> messages = new ArrayList<>();

    if (request.getSystemMessage() != null && !request.getSystemMessage().isBlank()) {
      messages.add(new SystemMessage(request.getSystemMessage()));
    }

    request.getMessages().stream()
        .map(m -> {
          if (m.getType() == MessageType.ASSISTANT) {
            return new AiMessage(m.getContent());
          } else {
            return new UserMessage(m.getContent());
          }
        })
        .forEach(messages::add);

    List<DataSource> dataSources = new ArrayList<>();
    if (request.getDataSources() != null) {
      dataSources = dataSourceRepository.findAllById(request.getDataSources())
          .stream().filter(d -> d.getUser().getId().equals(userService.getUser().getId()))
          .toList();
    }


    ChatMessageDto response = new ChatMessageDto();
    response.setContent(languageModel.generate(messages).content().text());
    response.setType(MessageType.ASSISTANT);
    return response;
  }
}
