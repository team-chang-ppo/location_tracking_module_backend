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
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiMeteringEventConsumer {
    private final ApiMeteringEventRepository apiMeteringEventRepository;
    private final ObjectMapper objectMapper;
    private final static String API_METERING_TOPIC = "api-metering-trace";

    @KafkaListener(topics = API_METERING_TOPIC, containerFactory = "defaultConsumerFactory")
    public void consume(List<String> event, Acknowledgment acknowledgment) {
        try {
            List<ApiMeteringEventDocument> documents = event.stream()
                    .map(this::createFromApiMeteringEvent)
                    .map(ApiMeteringEventDocument::createFromApiMeteringEvent)
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

    private ApiMeteringEvent createFromApiMeteringEvent(String event) {
        try {
            return objectMapper.readValue(event, ApiMeteringEvent.class);
        } catch (Exception e) {
            log.error("Failed to parse event: {}", event, e);
            throw new InvalidFormattedEventException(event);
        }
    }
}
