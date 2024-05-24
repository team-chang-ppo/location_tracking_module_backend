package org.changppo.monioring.server.metering;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

@Configuration
public class MeteringConfig {

    @Bean
    public ApiMeteringEventPublisher apiMeteringEventPublisher(
            ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate,
            ObjectMapper objectMapper
    ) {
        return new KafkaApiMeteringEventPublisher(reactiveKafkaProducerTemplate, objectMapper);
    }


    @Bean
    public ApiMeteringGatewayFilter apiMeteringGatewayFilterFactory(
            ApiMeteringEventPublisher apiMeteringEventPublisher
    ) {
        return new ApiMeteringGatewayFilter(apiMeteringEventPublisher);
    }
}
