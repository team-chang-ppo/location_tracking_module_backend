package org.changppo.monitoring.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.changppo.monitoring.dao.ApiEndpointIdFragment;
import org.changppo.monitoring.dao.ApiEndpointRepository;
import org.changppo.monitoring.dao.HourlyApiUsageRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Nested
    @DisplayName("consume 메서드")
    class consume {
        ObjectMapper objectMapper = createObjectMapper();
        static ValidatorFactory factory;
        Validator validator;
        @Mock
        ApiEndpointRepository apiEndpointRepository;
        @Mock
        HourlyApiUsageRepository hourlyApiUsageRepository;
        @Mock
        Acknowledgment acknowledgment;

        public ObjectMapper createObjectMapper(){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper;
        }

        @BeforeEach
        void setUp() {
            validator = factory.getValidator();
            lenient().doReturn(List.of(new ApiEndpointIdFragment() {
                @Override
                public Long getApiEndpointId() {
                    return 1L;
                }
            })).when(apiEndpointRepository).findAllByApiEndpointIdIn(any());
        }

        @BeforeAll
        static void beforeAll() {
            factory = Validation.buildDefaultValidatorFactory();
        }

        @AfterAll
        static void afterAll() {
            factory.close();
        }



        @Test
        @DisplayName("메시지를 정상적으로 소비한다.")
        void consumeMessage() {
            // given
            String kafkaValue = """
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
            List<String> values = List.of(kafkaValue);

            KafkaConsumer kafkaConsumer = new KafkaConsumer(objectMapper, validator, apiEndpointRepository, hourlyApiUsageRepository);

            // when
            kafkaConsumer.consume(values, acknowledgment);

            // then
            // hourlyApiUsageRepository.saveAll(entities)가 호출되었는지 확인한다.
            verify(hourlyApiUsageRepository, times(1)).saveAll(argThat(entities -> {
                if (entities.size() != 1) {
                    return false;
                }
                return entities.get(0).getApiEndpointId() == 1;
            }));
            // acknowledgment.acknowledge()가 호출되었는지 확인한다.
            verify(acknowledgment, times(1)).acknowledge();
        }

        @Test
        @DisplayName("json 형식이 잘못된 경우 그냥 무시하고 ack를 보낸다.")
        void consumeInvalidJson() {
            // given
            String kafkaValue = "invalid json";
            List<String> values = List.of(kafkaValue);

            KafkaConsumer kafkaConsumer = new KafkaConsumer(objectMapper, validator, apiEndpointRepository, hourlyApiUsageRepository);

            // when
            kafkaConsumer.consume(values, acknowledgment);

            // then
            // hourlyApiUsageRepository.saveAll(entities)가 호출되지 않았는지 확인한다.
            verify(hourlyApiUsageRepository, never()).saveAll(any());
            // acknowledgment.acknowledge()가 호출되었는지 확인한다.
            verify(acknowledgment, times(1)).acknowledge();
        }

        @Test
        @DisplayName("json 형식은 맞지만 유효하지 않은 경우 그냥 무시하고 ack를 보낸다.")
        void consumeInvalidEvent() {
            // given
            String kafkaValue = """
                    {
                      "apiEndpointId": 1,
                      "memberId": 1,
                      "apiKeyId": 1,
                      "date": "2024-05-18",
                      "window": {
                        "start": "2024-05-18T05:24:00.000Z",
                        "end": "2024-05-18T05:26:00.000Z"
                      },
                      "count": -1
                    }
                    """;
            List<String> values = List.of(kafkaValue);

            KafkaConsumer kafkaConsumer = new KafkaConsumer(objectMapper, validator, apiEndpointRepository, hourlyApiUsageRepository);

            // when
            kafkaConsumer.consume(values, acknowledgment);

            // then
            // hourlyApiUsageRepository.saveAll(entities)가 호출되지 않았는지 확인한다.
            verify(hourlyApiUsageRepository, never()).saveAll(any());
            // acknowledgment.acknowledge()가 호출되었는지 확인한다.
            verify(acknowledgment, times(1)).acknowledge();
        }

        @Test
        @DisplayName("apiEndpointId가 유효하지 않은 경우, apiEndpointId를 null로 처리한다.")
        void consumeInvalidApiEndpointId() {
            // given
            String kafkaValue = """
                    {
                      "apiEndpointId": 2,
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
            List<String> values = List.of(kafkaValue);

            KafkaConsumer kafkaConsumer = new KafkaConsumer(objectMapper, validator, apiEndpointRepository, hourlyApiUsageRepository);

            // when
            kafkaConsumer.consume(values, acknowledgment);

            // then
            // hourlyApiUsageRepository.saveAll(entities)가 호출되었는지 확인한다.
            verify(hourlyApiUsageRepository, times(1)).saveAll(argThat(entities -> {
                if (entities.size() != 1) {
                    return false;
                }
                return entities.get(0).getApiEndpointId() == null;
            }));
            // acknowledgment.acknowledge()가 호출되었는지 확인한다.
            verify(acknowledgment, times(1)).acknowledge();
        }
    }
}