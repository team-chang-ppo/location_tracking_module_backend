package org.changppo.monitoring.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.domain.HourlyApiUsageEvent;
import org.changppo.monitoring.dao.ApiEndpointIdFragment;
import org.changppo.monitoring.dao.ApiEndpointRepository;
import org.changppo.monitoring.dao.HourlyApiUsageEntity;
import org.changppo.monitoring.dao.HourlyApiUsageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final ApiEndpointRepository apiEndpointRepository;
    private final HourlyApiUsageRepository hourlyApiUsageRepository;

    @KafkaListener(topics = "hourly-api-usage", groupId = "monitoring-server", containerFactory = "defaultConsumerFactory")
    public void consume(List<String> values, Acknowledgment acknowledgment) {
        List<HourlyApiUsageEntity> entities = values.stream()
                .map(this::parseEvent)
                .filter(Objects::nonNull)
                .map(HourlyApiUsageEntity::from)
                .toList();
        if (entities.size() != values.size()) {
            log.warn("{} events are invalid", values.size() - entities.size());
        }

        if (entities.isEmpty()) {
            acknowledgment.acknowledge();
            return;
        }

        log.info("Received {} valid events", entities.size());

        /*
        모든 apiEndpointId가 유효한지 확인하고 유효하지 않은것은 null로 처리한다
         */
        List<Long> apiEndpointIds = entities.stream()
                .map(HourlyApiUsageEntity::getApiEndpointId)
                .toList();
        Set<Long> validApiEndpointIds = apiEndpointRepository.findAllByApiEndpointIdIn(apiEndpointIds)
                .stream()
                .map(ApiEndpointIdFragment::getApiEndpointId)
                .collect(Collectors.toSet());
        entities.forEach(entity -> {
            if (!validApiEndpointIds.contains(entity.getApiEndpointId())) {
                entity.setApiEndpointId(null);
            }
        });
        try {
            hourlyApiUsageRepository.saveAll(entities);
            acknowledgment.acknowledge();
            return;
        } catch (Exception e) {
            log.error("Failed to save hourly api usage", e);
            throw e;
        }
    }

    protected HourlyApiUsageEvent parseEvent(@NotNull String value) {
        try {
            HourlyApiUsageEvent event = objectMapper.readValue(value, HourlyApiUsageEvent.class);
            var violations = validator.validate(event);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException("Invalid event: " + violations);
            }
            return event;
        } catch (JsonProcessingException e) {
            // 일단 형태가 잘못된 메시지는 무시하는 것으로 처리
            log.error("Failed to parse event", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid event", e);
        }
        return null;
    }

}
