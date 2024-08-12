package com.alexaytov.ai_hub.services.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.ai.AICoreModelOptions;
import com.alexaytov.ai_hub.ai.AICoreServiceChatModel;
import com.alexaytov.ai_hub.model.entities.AIModelType;
import com.alexaytov.ai_hub.services.AIService;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AIServiceImpl implements AIService {
    @Override
    public ChatLanguageModel getModel(AIModelType type, String apiKey, Map<String, String> parameters) {
        if (type == AIModelType.OPEN_AI) {
            return OpenAiChatModel.withApiKey(apiKey);
        }

        if (type == AIModelType.AI_CORE) {
            String deploymentId = parameters.get("deploymentId");
            if (deploymentId == null) {
                throw new HttpClientErrorException(BAD_REQUEST, "Deployment ID is required for AI Core model");
            }

            var options = buildAICoreOptions(parameters);
            return new AICoreServiceChatModel(apiKey, deploymentId, options);
        }

        throw new HttpClientErrorException(BAD_REQUEST, "Unknown model type");
    }

    private static AICoreModelOptions buildAICoreOptions(Map<String, String> parameters) {
        var optionsBuilder = new AICoreModelOptions.Builder();

        String presencePenalty = parameters.get("presencePenalty");
        if (presencePenalty != null) {
            optionsBuilder.presencePenalty(Float.parseFloat(presencePenalty));
        }
        String frequencyPenalty = parameters.get("frequencyPenalty");
        if (frequencyPenalty != null) {
            optionsBuilder.frequencyPenalty(Float.parseFloat(frequencyPenalty));
        }
        String maxTokens = parameters.get("maxTokens");
        if (maxTokens != null) {
            optionsBuilder.maxTokens(Integer.parseInt(maxTokens));
        }
        String temperature = parameters.get("temperature");
        if (temperature != null) {
            optionsBuilder.temperature(Integer.parseInt(temperature));
        }
        String topP = parameters.get("topP");
        if (topP != null) {
            optionsBuilder.topP(Integer.parseInt(topP));
        }
        String stop = parameters.get("stop");
        if (stop != null) {
            optionsBuilder.stop(stop);
        }
        return optionsBuilder.build();
    }
}
