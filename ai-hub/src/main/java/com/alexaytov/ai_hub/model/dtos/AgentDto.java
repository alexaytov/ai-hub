package com.alexaytov.ai_hub.model.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public class AgentDto {

  private Long id;

  @NotNull
  @Length(max = 50)
  private String name;

  @NotNull
  @Length(max = 255)
  private String description;

  @NotNull
  private Long modelId;
  @NotNull
  private Long systemMessageId;

  private List<Long> dataSources;

  public List<Long> getDataSources() {
    return dataSources;
  }

  public void setDataSources(List<Long> dataSources) {
    this.dataSources = dataSources;
  }

  public @NotNull @Length(max = 50) String getName() {
    return name;
  }

  public void setName(@NotNull @Length(max = 50) String name) {
    this.name = name;
  }

  public @NotNull @Length(max = 255) String getDescription() {
    return description;
  }

  public void setDescription(@NotNull @Length(max = 255) String description) {
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Long getSystemMessageId() {
    return systemMessageId;
  }

  public void setSystemMessageId(Long systemMessageId) {
    this.systemMessageId = systemMessageId;
  }
}
