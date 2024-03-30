package org.changppo.cost_management_service.builder.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

public class JsonResponseBuilder {

    private final JsonNode jsonResponse;

    private JsonResponseBuilder(MvcResult result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.jsonResponse = objectMapper.readTree(result.getResponse().getContentAsString());
    }

    public static JsonResponseBuilder buildJsonResponse(MvcResult result) throws IOException {
        return new JsonResponseBuilder(result);
    }

    public Long getLongValue(String... fieldPath) {
        JsonNode currentNode = jsonResponse;
        for (String field : fieldPath) {
            currentNode = currentNode.path(field);
        }
        return currentNode.asLong();
    }

    public String getStringValue(String... fieldPath) {
        JsonNode currentNode = jsonResponse;
        for (String field : fieldPath) {
            currentNode = currentNode.path(field);
        }
        return currentNode.asText();
    }
}