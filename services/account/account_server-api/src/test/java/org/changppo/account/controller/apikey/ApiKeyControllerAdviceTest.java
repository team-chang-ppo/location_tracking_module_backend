package org.changppo.account.controller.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.apikey.GradeNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.response.exception.common.ExceptionAdvice;
import org.changppo.account.response.exception.common.ResponseHandler;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApiKeyControllerAdviceTest {

    @InjectMocks
    ApiKeyController apiKeyController;
    @Mock
    ApiKeyService apiKeyService;
    @Mock
    ResponseHandler responseHandler;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiKeyController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void createFreeKeyMemberNotFoundExceptionTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);
        doThrow(new MemberNotFoundException()).when(apiKeyService).createFreeKey(any());

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createFreeKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFreeKeyGradeNotFoundExceptionTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);
        doThrow(new GradeNotFoundException()).when(apiKeyService).createFreeKey(any());

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createFreeKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClassicKeyMemberNotFoundExceptionTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);
        doThrow(new MemberNotFoundException()).when(apiKeyService).createClassicKey(any());

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createClassicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClassicKeyGradeNotFoundExceptionTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);
        doThrow(new GradeNotFoundException()).when(apiKeyService).createClassicKey(any());

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createClassicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void readApiKeyNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new ApiKeyNotFoundException()).when(apiKeyService).read(any());

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteApiKeyNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new ApiKeyNotFoundException()).when(apiKeyService).delete(any());

        // when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}