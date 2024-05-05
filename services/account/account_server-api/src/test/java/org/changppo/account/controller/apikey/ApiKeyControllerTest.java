package org.changppo.account.controller.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.service.apikey.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.changppo.account.builder.apikey.ApiKeyRequestBuilder.buildApiKeyCreateRequest;
import static org.changppo.account.builder.apikey.ApiKeyRequestBuilder.buildApiKeyReadAllRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApiKeyControllerTest {

    @InjectMocks
    private ApiKeyController apiKeyController;
    @Mock
    private ApiKeyService apiKeyService;
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiKeyController).build();
    }

    @Test
    void createFreeKeyTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createFreeKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(apiKeyService).createFreeKey(any(ApiKeyCreateRequest.class));
    }

    @Test
    void createClassicKeyTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createClassicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(apiKeyService).createClassicKey(any(ApiKeyCreateRequest.class));
    }

    @Test
    void readTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1/{id}", id))
                .andExpect(status().isOk());

        verify(apiKeyService).read(eq(id));
    }

    @Test
    void readAllTest() throws Exception {
        // given
        Long id = 1L;
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, 10);

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1/member/{id}", id)
                        .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                        .param("size", req.getSize().toString()))
                .andExpect(status().isOk());

        verify(apiKeyService).readAll(eq(id), any(ApiKeyReadAllRequest.class));
    }

    @Test
    void deleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", id))
                .andExpect(status().isOk());

        verify(apiKeyService).delete(eq(id));
    }
}
