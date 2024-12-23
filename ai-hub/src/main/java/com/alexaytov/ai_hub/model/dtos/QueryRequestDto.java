package com.alexaytov.ai_hub.model.dtos;

import java.util.List;
import java.util.Map;

public class QueryRequestDto {

  private Long modelId;
  private String systemMessage;
  private List<ChatMessageDto> messages;
  private List<Long> dataSources;
  private Map<String, String> customParameters;

  public void setCustomParameters(Map<String, String> customParameters) {
    this.customParameters = customParameters;
  }

  public List<Long> getDataSources() {
    return dataSources;
  }

  public void setDataSources(List<Long> dataSources) {
    this.dataSources = dataSources;
  }

  public Map<String, String> getCustomParameters() {
    return customParameters;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public String getSystemMessage() {
    return systemMessage;
  }

  public List<ChatMessageDto> getMessages() {
    return messages;
  }

  public void setSystemMessage(String systemMessage) {
    this.systemMessage = systemMessage;
  }

  public void setMessages(List<ChatMessageDto> messages) {
    this.messages = messages;
  }
}
