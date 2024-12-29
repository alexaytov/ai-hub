package com.alexaytov.ai_hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chromadb")
public class ChromaDB {
  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }
}
