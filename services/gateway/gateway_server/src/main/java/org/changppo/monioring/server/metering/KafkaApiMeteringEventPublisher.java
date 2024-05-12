package org.changppo.monioring.server.metering;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.GatewayConstant;
import org.changppo.monioring.domain.ApiUsageEventPayLoad;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class KafkaApiMeteringEventPublisher implements ApiMeteringEventPublisher {
    private final ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publish(ApiUsageEventPayLoad apiUsageEventPayLoad) {
        try {
            final String json = objectMapper.writeValueAsString(apiUsageEventPayLoad);
            return kafkaProducerTemplate.send(
                    GatewayConstant.API_METERING_TOPIC,
                    json
            ).flatMap(result -> {
                if (result.exception() != null) {
                    return Mono.error(result.exception());
                }
                return Mono.empty();
            });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
