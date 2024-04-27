package org.changppo.tracking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

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
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
