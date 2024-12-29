package com.alexaytov.ai_hub.model.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "agents")
public class Agent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 255)
  private String description;

  @ManyToOne
  @JoinColumn(name = "model_id", nullable = false)
  private AIModel model;

  @ManyToOne
  @JoinColumn(name = "message_id", nullable = false)
  private SystemMessage systemMessage;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany
  private List<DataSource> dataSources;

  public List<DataSource> getDataSources() {
    return dataSources;
  }

  public void setDataSources(List<DataSource> dataSources) {
    this.dataSources = dataSources;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AIModel getModel() {
    return model;
  }

  public void setModel(AIModel model) {
    this.model = model;
  }

  public SystemMessage getSystemMessage() {
    return systemMessage;
  }

  public void setSystemMessage(SystemMessage systemMessage) {
    this.systemMessage = systemMessage;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
