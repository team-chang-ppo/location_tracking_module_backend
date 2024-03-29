package org.changppo.tracking.api;

import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.base.WithCustomMockUser;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.tracking.api.request.ConnectRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.ConnectResponse;
import org.changppo.tracking.service.TrackingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {TrackingController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class TrackingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingService trackingService;

    private final String baseUrl = "/api/tracking/v1";

    @Nested
    @DisplayName("<유저 프로필 조회>")
    class mvpTest {
        @DisplayName("연결을 성공함")
        @Test
        void successConnect() throws Exception {
            // given
            ConnectRequest request = new ConnectRequest("1",new Point(1,1), new Point(2,2), 3L);
            ConnectResponse response = new ConnectResponse("TOKEN");
            given(trackingService.connect(any(ConnectRequest.class))).willReturn(response);

            //when
            mockMvc.perform(post(baseUrl + "/connect")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
                    .andDo(print());
        }

        @DisplayName("Tracking 성공함")
        @WithCustomMockUser
        @Test
        void successTracking() throws Exception {
            // given
            TrackingRequest request = new TrackingRequest(new Point(1,2));

            //when
            mockMvc.perform(post(baseUrl + "/tracking")
                            .header("Authorization", "Bearer {ACCESS_TOKEN}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    //then
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @DisplayName("Tracking 성공적으로 마침")
        @WithCustomMockUser
        @Test
        void successTrackingFinish() throws Exception {
            // given

            //when
            mockMvc.perform(delete(baseUrl + "/tracking")
                            .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                    //then
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @DisplayName("Tracking 정보 받아오기 성공")
        @WithCustomMockUser
        @Test
        void successGetTracking() throws Exception {
            // given
            TrackingResponse response = new TrackingResponse(new Point(1, 2));
            given(trackingService.getTracking(any())).willReturn(response);

            //when
            mockMvc.perform(get(baseUrl + "/tracking")
                            .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.locations.x").value(1))
                    .andExpect(jsonPath("$.locations.y").value(2))
                    .andDo(print());
        }
    }
}