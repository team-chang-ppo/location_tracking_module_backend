package org.changppo.monioring.server.metering;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.changppo.monioring.server.GatewayConstant;
import org.changppo.monioring.domain.ApiMeteringEventPayLoad;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class KafkaApiMeteringEventPublisher implements ApiMeteringEventPublisher {
    private final ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publish(ApiMeteringEventPayLoad apiMeteringEventPayLoad) {
        try {
            final String json = objectMapper.writeValueAsString(apiMeteringEventPayLoad);
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
