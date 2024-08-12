package com.alexaytov.ai_hub.ai;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alexaytov.ai_hub.ai.data.Choice;
import com.alexaytov.ai_hub.ai.data.CompletionRequest;
import com.alexaytov.ai_hub.ai.data.CompletionRequestMessage;
import com.alexaytov.ai_hub.ai.data.CompletionResponse;
import com.alexaytov.ai_hub.ai.data.ToolCall;
import com.alexaytov.ai_hub.ai.data.ToolCallFunction;
import com.alexaytov.ai_hub.ai.exceptions.ChatCompletionException;
import com.alexaytov.ai_hub.ai.messages.AssistantMessage;
import com.alexaytov.ai_hub.ai.messages.ChoiceMessage;
import com.alexaytov.ai_hub.ai.messages.ToolMessage;
import com.alexaytov.ai_hub.ai.token.ClientCredentialsTokenGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.ContentType;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.String.format;

public class AICoreServiceChatModel implements ChatLanguageModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(AICoreServiceChatModel.class);

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String url;
    private final ClientCredentialsTokenGenerator tokenGenerator;
    private final AICoreModelOptions options;

    public AICoreServiceChatModel(String apiKey,
                                  String deploymentId,
                                  AICoreModelOptions options) {
        try {
            Map<String, Object> parsedApiKey = MAPPER.readValue(apiKey, Map.class);
            tokenGenerator = buildTokenGenerator(parsedApiKey);
            this.options = options;

            String apiUrl = buildApiUrl(parsedApiKey);
            url = buildUrl(deploymentId, apiUrl);
        } catch (JsonProcessingException | ClassCastException e) {
            throw new ChatCompletionException("Failed to parse API key", e);
        }

    }

    private static String buildApiUrl(Map<String, Object> apiKey) {
        try {
            return ((Map<String, String>) apiKey.get("serviceurls")).get("AI_API_URL");
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Invalid API key format", ex);
        }
    }

    private static String buildUrl(String deploymentId, String apiUrl) {
        return format("%s/v2/inference/deployments/%s/chat/completions?api-version=2024-03-01-preview", apiUrl, deploymentId);
    }

    private static ClientCredentialsTokenGenerator buildTokenGenerator(Map<String, Object> apiKey) {
        String clientId = apiKey.get("clientid").toString();
        String clientSecret = apiKey.get("clientsecret").toString();
        String tokenUrl = apiKey.get("url") + "/oauth/token";

        return new ClientCredentialsTokenGenerator(clientId, clientSecret, tokenUrl);
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        CompletionRequest request = buildRequest(messages);
        HttpEntity<String> entity = buildEntity(request);
        return executeCompletion(entity);
    }

    private Response<AiMessage> executeCompletion(HttpEntity<String> entity) {
        try {
            LOGGER.info("Sending completion request to {} with body {}", url, entity.getBody());
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.POST, entity, String.class);
            LOGGER.info("Received completion response: {}", response.getBody());
            return buildResponse(response);
        } catch (RestClientException ex) {
            throw new ChatCompletionException("Failed to complete chat request", ex);
        }
    }

    private static Response<AiMessage> buildResponse(ResponseEntity<String> response) {
        try {
            CompletionResponse completionResponse = MAPPER.readValue(response.getBody(), CompletionResponse.class);
            ResponseResult result = buildResponseResult(completionResponse.choices());

            TokenUsage usage = new TokenUsage(
                completionResponse.usage().promptTokens(),
                completionResponse.usage().completionTokens(),
                completionResponse.usage().totalTokens()
            );

            FinishReason reason = buildFinishReason(result.finishReason());

            return new Response<>(result.message(), usage, reason);
        } catch (JsonProcessingException e) {
            throw new ChatCompletionException("Failed to deserialize completion response", e);
        }
    }

    private static ResponseResult buildResponseResult(List<Choice> choices) {
        AiMessage message;
        String finishReason = "";

        if (choices.isEmpty()) {
            message = new AiMessage("");
        } else {
            Choice choice = choices.get(0);
            finishReason = choice.finishReason();

            ChoiceMessage choiceMessage = choice.message();
            if (choiceMessage.toolCalls() == null || choiceMessage.toolCalls().isEmpty()) {
                message = new AiMessage(choiceMessage.content());
            } else {
                List<ToolExecutionRequest> executionRequests = choiceMessage.toolCalls().stream()
                    .map(toolCal -> ToolExecutionRequest.builder()
                        .id(toolCal.id())
                        .name(toolCal.function().name())
                        .arguments(toolCal.function().arguments())
                        .build())
                    .toList();

                if (choiceMessage.content() != null) {
                    message = new AiMessage(choiceMessage.content(), executionRequests);
                } else {
                    message = new AiMessage(executionRequests);
                }
            }
        }

        return new ResponseResult(message, finishReason);
    }

    private record ResponseResult(AiMessage message, String finishReason) {
    }

    private static FinishReason buildFinishReason(String finishReason) {
        try {
            return FinishReason.valueOf(finishReason.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return FinishReason.OTHER;
        }
    }

    private CompletionRequest buildRequest(List<ChatMessage> messages) {
        List<CompletionRequestMessage> requestMessages = messages.stream()
            .flatMap(AICoreServiceChatModel::toMessage)
            .toList();

        CompletionRequest request = new CompletionRequest(
            requestMessages,
            options.getMaxTokens(),
            options.getTemperature(),
            options.getFrequencyPenalty(),
            options.getPresencePenalty(),
            options.getTopP(),
            options.getStop(),
            List.of()
        );

        return request;
    }

    private static Stream<CompletionRequestMessage> toMessage(ChatMessage msg) {
        if (msg.type().equals(ChatMessageType.SYSTEM)) {
            SystemMessage sysMsg = (SystemMessage) msg;
            return Stream.of(new com.alexaytov.ai_hub.ai.messages.SystemMessage(sysMsg.text()));
        }

        if (msg.type().equals(ChatMessageType.USER)) {
            UserMessage userMsg = (UserMessage) msg;
            // Supports only text content types for now
            return userMsg.contents().stream()
                .filter(content -> content.type() == ContentType.TEXT)
                .map(content -> new com.alexaytov.ai_hub.ai.messages.UserMessage(((TextContent) content).text()));
        }

        if (msg.type().equals(ChatMessageType.AI)) {
            AiMessage aiMsg = (AiMessage) msg;

            if (aiMsg.toolExecutionRequests() == null || aiMsg.toolExecutionRequests().isEmpty()) {
                return Stream.of(new AssistantMessage(aiMsg.text()));
            }

            List<ToolCall> toolCalls = aiMsg.toolExecutionRequests().stream()
                .map(tool -> new ToolCall(tool.id(), "function", new ToolCallFunction(tool.name(), tool.arguments())))
                .toList();

            return Stream.of(new AssistantMessage(aiMsg.text(), toolCalls));
        }

        if (msg.type().equals(ChatMessageType.TOOL_EXECUTION_RESULT)) {
            ToolExecutionResultMessage toolMsg = (ToolExecutionResultMessage) msg;
            return Stream.of(new ToolMessage(toolMsg.text(), toolMsg.id()));
        }

        throw new IllegalArgumentException("Unsupported message type: " + msg.type());
    }

    private HttpEntity<String> buildEntity(CompletionRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put("Authorization", List.of("Bearer " + tokenGenerator.generateToken()));
            headers.put("Content-Type", List.of("application/json"));
            headers.put("Accept", List.of("application/json"));
            headers.put("AI-Resource-Group", List.of("default"));

            return new HttpEntity<>(MAPPER.writeValueAsString(request), headers);
        } catch (JsonProcessingException e) {
            throw new ChatCompletionException("Failed to serialize completion request", e);
        }
    }
}
