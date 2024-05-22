package org.changppo.monioring.server.metering;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

public class MeteringConfig {

    @Bean
    public ApiMeteringEventPublisher apiMeteringEventPublisher(
            ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate,
            ObjectMapper objectMapper
    ) {
        return new KafkaApiMeteringEventPublisher(kafkaProducerTemplate, objectMapper);
    }


    @Bean
    public ApiMeteringGatewayFilter apiMeteringGatewayFilterFactory(
            ApiMeteringEventPublisher apiMeteringEventPublisher
    ) {
        return new ApiMeteringGatewayFilter(apiMeteringEventPublisher);
    }
}
