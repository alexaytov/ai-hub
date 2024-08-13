package com.alexaytov.ai_hub.services.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.ai.AICoreServiceChatModel;
import com.alexaytov.ai_hub.model.entities.AIModelType;

import dev.langchain4j.model.openai.OpenAiChatModel;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AIServiceImplTest {

    AIServiceImpl classUnderTest = new AIServiceImpl();

    @Test
    void givenUnknownModelType_whenGettingModel_thenCorrectExceptionIsThrown() {
        assertThrows(HttpClientErrorException.class, () -> classUnderTest.getModel(null, null, null));
    }

    @Test
    void givenAICoreModelWithoutDeploymentId_whenGettingModel_thenCorrectExceptionIsThrown() {
        Map<String, String> params = Map.of();
        assertThrows(HttpClientErrorException.class, () -> classUnderTest.getModel(AIModelType.AI_CORE, null, params));
    }

    @Test
    void givenAICoreModelWithDeploymentId_whenGettingModel_thenCorrectModelIsReturned() {
        Map<String, String> params = Map.of("deploymentId", "123");
        var model = classUnderTest.getModel(AIModelType.AI_CORE, """
            {
            "clientid": "1",
            "clientsecret": "secret",
            "url": "url",
            "serviceurls": {"AI_API_URL": "url"}
            }
            """, params);

        assertInstanceOf(AICoreServiceChatModel.class, model);
    }

    @Test
    void givenOpenAiModel_whenGettingModel_thenCorrectModelIsReturned() {
        var model = classUnderTest.getModel(AIModelType.OPEN_AI, "{}", Map.of());
        assertInstanceOf(OpenAiChatModel.class, model);
    }
}