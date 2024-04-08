package org.changppo.monitoring.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monitoring.ApiMeteringEvent;
import org.changppo.monitoring.ApiMeteringEventDocument;
import org.changppo.monitoring.ApiMeteringEventRepository;
import org.changppo.monitoring.exception.InvalidFormattedEventException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiMeteringEventConsumer {
    private final ApiMeteringEventRepository apiMeteringEventRepository;
    private final ObjectMapper objectMapper;
    private final static String API_METERING_TOPIC = "api-metering-trace";

    @KafkaListener(topics = API_METERING_TOPIC, containerFactory = "defaultConsumerFactory")
    public void consume(@Payload List<Message<String>> events, Acknowledgment acknowledgment) {
        try {
            List<ApiMeteringEventDocument> documents = events.stream()
                    .map(this::createFromApiMeteringEvent)
                    .toList();
            apiMeteringEventRepository.saveAll(documents);
            acknowledgment.acknowledge();
        } catch (InvalidFormattedEventException e) {
            // TODO : 어떻게 처리할까
        } catch (IllegalArgumentException e) {
            // TODO : 어떻게 처리할까
        } catch (Exception e) {
            // TODO : 어떻게 처리할까
        }
    }

    private ApiMeteringEventDocument createFromApiMeteringEvent(Message<String> event) {
        try {
            //발송된 timestamp
            Long timestampLong = event.getHeaders().get(KafkaHeaders.RECEIVED_TIMESTAMP, Long.class);
            Instant timestamp;
            if (timestampLong != null) {
                timestamp = Instant.ofEpochMilli(timestampLong);
            } else {
                log.warn("Timestamp is not found in the event. Set current time as timestamp.");
                timestamp = Instant.now();
            }
            // payload
            String payload = event.getPayload();
            ApiMeteringEvent apiMeteringEvent = objectMapper.readValue(payload, ApiMeteringEvent.class);

            return ApiMeteringEventDocument.createFromApiMeteringEvent(apiMeteringEvent, timestamp);
        } catch (Exception e) {
            log.error("Failed to parse event: {}", event, e);
            throw new InvalidFormattedEventException("Failed to parse event");
        }
    }
}
