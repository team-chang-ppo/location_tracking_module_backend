package org.changppo.cost_management_service.builder.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

public class JsonNodeBuilder {
    private final JsonNode jsonNode;

    private JsonNodeBuilder(MvcResult result, ObjectMapper objectMapper) throws IOException {
        this.jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
    }

    public static JsonNodeBuilder buildJsonNode(MvcResult result, ObjectMapper objectMapper) throws IOException {
        return new JsonNodeBuilder(result, objectMapper);
    }

    public Long getLongValue(String... fieldPath) {
        JsonNode currentNode = jsonNode;
        for (String field : fieldPath) {
            currentNode = currentNode.path(field);
        }
        return currentNode.asLong();
    }

    public String getStringValue(String... fieldPath) {
        JsonNode currentNode = jsonNode;
        for (String field : fieldPath) {
            currentNode = currentNode.path(field);
        }
        return currentNode.asText();
    }
}