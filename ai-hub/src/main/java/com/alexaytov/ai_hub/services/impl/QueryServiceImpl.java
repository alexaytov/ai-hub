package com.alexaytov.ai_hub.services.impl;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.alexaytov.ai_hub.config.ChromaDB;
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
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class QueryServiceImpl implements QueryService {

  private final UserService userService;
  private final Encryption encryption;
  private final ModelRepository modelRepository;
  private final AIService aiService;
  private final DataSourceRepository dataSourceRepository;
  private final ChromaDB chromaDB;

  public QueryServiceImpl(
          UserService userService,
          Encryption encryption,
          ModelRepository modelRepository,
          AIService aiService,
          DataSourceRepository dataSourceRepository, ChromaDB chromaDB) {
    this.userService = userService;
    this.encryption = encryption;
    this.modelRepository = modelRepository;
    this.aiService = aiService;
    this.dataSourceRepository = dataSourceRepository;
      this.chromaDB = chromaDB;
  }

  @Override
  public ChatMessageDto query(QueryRequestDto request) {
    AIModel model =
        modelRepository
            .findById(request.getModelId())
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Model not found"));
    String apiKey = encryption.decrypt(model.getApiKey());

    Map<String, String> defaultParameters = model.getParameters();
    defaultParameters.putAll(request.getCustomParameters());

    ChatLanguageModel languageModel =
        aiService.getModel(model.getType().getType(), apiKey, defaultParameters);
    List<ChatMessage> messages = new ArrayList<>();

    if (request.getSystemMessage() != null && !request.getSystemMessage().isBlank()) {
      messages.add(new SystemMessage(request.getSystemMessage()));
    }

    request.getMessages().stream()
        .map(
            m -> {
              if (m.getType() == MessageType.ASSISTANT) {
                return new AiMessage(m.getContent());
              } else {
                return new UserMessage(m.getContent());
              }
            })
        .forEach(messages::add);

    List<DataSource> dataSources = new ArrayList<>();
    if (request.getDataSources() != null) {
      dataSources =
          dataSourceRepository.findAllById(request.getDataSources()).stream()
              .filter(d -> d.getUser().getId().equals(userService.getUser().getId()))
              .toList();
    }

    ChromaEmbeddingStore store =
        ChromaEmbeddingStore.builder()
            .collectionName("user_" + userService.getUser().getId())
            .baseUrl(chromaDB.getHost())
            .build();
    EmbeddingStoreContentRetriever retriever =
        EmbeddingStoreContentRetriever.builder()
            .embeddingModel(new AllMiniLmL6V2EmbeddingModel())
            .embeddingStore(store)
            .maxResults(3)
            .minScore(0.75)
            .filter(
                metadataKey("name")
                    .isIn(dataSources.stream().map(DataSource::getFileName).toList()))
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(languageModel)
            .contentRetriever(retriever)
            .build();

    Result<String> chat = assistant.chat(messages.get(messages.size() - 1).text());

    ChatMessageDto response = new ChatMessageDto();
    response.setContent(chat.content());
    response.setType(MessageType.ASSISTANT);
    return response;
  }
}
