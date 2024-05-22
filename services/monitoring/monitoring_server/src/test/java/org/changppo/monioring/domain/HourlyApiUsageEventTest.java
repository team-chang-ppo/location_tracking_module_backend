package org.changppo.monioring.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HourlyApiUsageEventTest {

    @Test
    void jacksonMappingTest() throws JsonProcessingException {
        String json =
        """
        {
          "apiEndpointId": 1,
          "memberId": 1,
          "apiKeyId": 1,
          "date": "2024-05-18",
          "window": {
            "start": "2024-05-18T05:24:00.000Z",
            "end": "2024-05-18T05:26:00.000Z"
          },
          "count": 6
        }
        """;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        HourlyApiUsageEvent event = mapper.readValue(json, HourlyApiUsageEvent.class);

        assertEquals(1, event.getApiEndpointId());
        assertEquals(1, event.getMemberId());
        assertEquals(1, event.getApiKeyId());
        assertEquals(LocalDate.of(2024, 5, 18), event.getDate());
        assertEquals(6, event.getCount());
        assertEquals("2024-05-18T05:24", event.getWindow().getStart().toString());
        assertEquals("2024-05-18T05:26", event.getWindow().getEnd().toString());


    }

}