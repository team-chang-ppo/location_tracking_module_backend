package org.changppo.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

/*
=======================================================
이거 전체적으로 다시만들어야함
Spring Batch 와 카프카 고민중
=======================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamConsumer implements InitializingBean{
    private final RedisTemplate<String, String> redisTemplate;
    private final MongoTemplate mongoTemplate;
    private final ApiRecordRepository apiRecordRepository;
    private final ObjectMapper objectMapper;
    // @see Gateway's GatewayConstant

    private static final String API_METERING_QUEUE_KEY = "api_metering_queue";
    private static final Integer BATCH_SIZE = 50;

    @Value("${spring.application.name}")
    private String applicationName;
    private final String random = UUID.randomUUID().toString();

    // 없으면 5초간 backoff
    @Scheduled(fixedDelay = 5000)
    public void consume() {
        long left = 1;
        while (true) {
            // 읽어온다.
            List<String> items = redisTemplate.opsForList()
                    .rightPop(API_METERING_QUEUE_KEY, BATCH_SIZE);

            // 비어있으면 끝
            if (items == null || items.isEmpty()) {
                break;
            }

            // 처리
            try {
                left = process(items);
                // 남은게 없으면 끝
                if (left == 0) {
                    break;
                }
            } catch (Exception e) {
                // 실패하면 다시 넣는다.
                handleFailure(e, items);
            }

        }

    }

    /**
     * @return 남은 데이터의 갯수
     * @throws Exception 처리중 에러 발생시
     */
    protected long process(List<String> items) throws Exception{

        // json 파싱
        int size = items.size();
        List<ApiRecord> records = new ArrayList<>(size);
        for (String item : items) {
            ApiRecord record = objectMapper.readValue(item, ApiRecord.class);
            records.add(record);
        }

        // 저장
        List<ApiRecordDocument> documents = records.stream()
                .map(this::convertToDocument)
                .toList();
        apiRecordRepository.saveAll(documents);
        // 남은 사이즈 체크
        Long leftSize = redisTemplate.opsForList().size(API_METERING_QUEUE_KEY);
        return leftSize == null ? 0 : leftSize;
    };

    protected void handleFailure(Exception e, List<String> items) {
        log.error("Failed to process", e);
        redisTemplate.opsForList().rightPushAll(API_METERING_QUEUE_KEY, items);
        //TODO 일단은 이렇게 처리하고, 추후에 batch로 개선
    }

    protected ApiRecordDocument convertToDocument(ApiRecord messages) {
        Long apiKeyId = messages.apiKeyId();
        String routeId = messages.routeId();
        Instant timestamp = messages.timestamp();
        Map<String, String> details = messages.details();
        return new ApiRecordDocument(apiKeyId, routeId, timestamp, details);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // check mongodb connection
    }
}
