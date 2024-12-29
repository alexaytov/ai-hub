package com.alexaytov.ai_hub.services.impl;

import dev.langchain4j.service.Result;

public interface Assistant {
  Result<String> chat(String userMessage);
}
