package org.changppo.monioring.server.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.changppo.monioring.server.GatewayConstant;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

@Profile("local")
@Configuration(proxyBeanMethods = false)
public class KafkaConfig {

    @Bean
    public NewTopic apiMeteringTopic() {
        return TopicBuilder.name(GatewayConstant.API_METERING_TOPIC)
                .build();
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate(
            KafkaProperties props
    ) {
        return new ReactiveKafkaProducerTemplate<>(
                SenderOptions.create(props.buildProducerProperties())
        );
    }

}
