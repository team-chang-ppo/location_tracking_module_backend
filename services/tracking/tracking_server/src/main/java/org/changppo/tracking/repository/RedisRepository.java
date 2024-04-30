package org.changppo.tracking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void rightPush(String key, Object entity) {
        redisTemplate.opsForList().rightPush(key, entity);
    }

    public Object getTail(String key) {
        return redisTemplate.opsForList().index(key, -1);
    }

    public List<Object> findAll(String key) {
        String luaScript =
                "local items = redis.call('LRANGE', KEYS[1], 0, -1);" +
                        "redis.call('DEL', KEYS[1]);" +
                        "return items;";

        DefaultRedisScript<List> script = new DefaultRedisScript<>(luaScript, List.class);

        try {
            List<Object> result = redisTemplate.execute(script, Collections.singletonList(key));
            return result;
        } catch (DataAccessException e) {
            throw new RuntimeException("Redis 실행 중 오류 발생", e);
        }
    }
}
