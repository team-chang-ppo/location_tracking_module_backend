package org.changppo.gateway;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.style.ToStringCreator;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

@Slf4j
@Getter @Setter
@RequiredArgsConstructor
public class ApiRedisRateLimiter implements ApiRateLimiter {

    /**
     * Redis Rate Limiter property name.
     */
    public static final String CONFIGURATION_PROPERTY_NAME = "redis-rate-limiter";

    /**
     * Replenish Rate Limit header name.
     */
    public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";

    /**
     * Burst Capacity header name.
     */
    public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

    /**
     * Requested Tokens header name.
     */
    public static final String REQUESTED_TOKENS_HEADER = "X-RateLimit-Requested-Tokens";
    /**
     * Remaining Rate Limit header name.
     */
    public static final String REMAINING_HEADER = "X-RateLimit-Remaining";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<List<Long>> script;
    private final ApiRateContextResolver rateContextResolver;

    // configuration properties
    /**
     * Whether or not to include headers containing rate limiter information, defaults to
     * true.
     */
    private boolean includeHeaders = true;

    /**
     * The name of the header that returns number of remaining requests during the current
     * second.
     */
    private String remainingHeader = REMAINING_HEADER;

    /** The name of the header that returns the replenish rate configuration. */
    private String replenishRateHeader = REPLENISH_RATE_HEADER;

    /** The name of the header that returns the burst capacity configuration. */
    private String burstCapacityHeader = BURST_CAPACITY_HEADER;

    /** The name of the header that returns the requested tokens configuration. */
    private String requestedTokensHeader = REQUESTED_TOKENS_HEADER;



    static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }


    @Override
    public Mono<Response> isAllowed(String routeId, String apiKey, Long requestedTokens) {
        Mono<ApiRateContext> apiRateContext = rateContextResolver.resolve(routeId, apiKey);
        return apiRateContext
                .flatMap(rateContext -> {
                    long replenishRate = rateContext.getReplenishRate();
                    long burstCapacity = rateContext.getBurstCapacity();

                    try {
                        return tryAcquireToken(apiKey, replenishRate, burstCapacity, requestedTokens);
                    } catch (Exception e) {
                        // redis에 의해 single point of failure가 되지 않도록 그냥 로그만 남기고 통과
                        log.error("Error determining if user allowed from redis", e);
                        return Mono.just(new Response(false, true, getHeaders(replenishRate, burstCapacity, requestedTokens, -1L)));
                    }
                })
                .switchIfEmpty(Mono.just(Response.API_KEY_NOT_FOUND));

    }

    protected Mono<Response> tryAcquireToken(String apikey, long replenishRate, long burstCapacity, long requestedTokens) {
        long ttl = timeToLive(replenishRate, burstCapacity, requestedTokens);
        long now = Instant.now().getEpochSecond();
        List<String> keys = getKeys(apikey);
        List<String> scriptArgs = Arrays.asList(
                String.valueOf(replenishRate), //ARGV[1]
                String.valueOf(burstCapacity), //ARGV[2]
                String.valueOf(now), //ARGV[3]
                String.valueOf(requestedTokens), //ARGV[4]
                String.valueOf(ttl) //ARGV[5]
        );
        return this.redisTemplate.execute(this.script, keys, scriptArgs)
                .onErrorResume(throwable -> {
                    if (log.isDebugEnabled()) {
                        log.debug("Error calling rate limiter lua", throwable);
                    }
                    return Flux.just(Arrays.asList(1L, -1L));
                }).reduce(new ArrayList<Long>(), (longs, l) -> {
                    longs.addAll(l);
                    return longs;
                }).map(results -> {
                    boolean apiKeyNotFound = false;
                    boolean allowed = results.get(0) == 1L;
                    Long tokensLeft = results.get(1);

                    Response response = new Response(apiKeyNotFound, allowed, getHeaders(replenishRate, burstCapacity, requestedTokens, tokensLeft));

                    if (log.isDebugEnabled()) {
                        log.debug("response: " + response);
                    }
                    return response;
                });
    }

    protected long timeToLive(long replenishRate, long burstCapacity, long requestedTokens) {
        long fillTime = burstCapacity / replenishRate;
        return fillTime * 2;
    }


    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("redisTemplate", redisTemplate)
                .append("script", script)
                .toString();
    }

    public Map<String, String> getHeaders(long replenishRateHeader, long burstCapacityHeader, long requestedTokensHeader, Long tokensLeft) {
        Map<String, String> headers = new HashMap<>();
        if (isIncludeHeaders()) {
            headers.put(this.remainingHeader, tokensLeft.toString());
            headers.put(this.replenishRateHeader, String.valueOf(replenishRateHeader));
            headers.put(this.burstCapacityHeader, String.valueOf(burstCapacityHeader));
            headers.put(this.requestedTokensHeader, String.valueOf(requestedTokensHeader));
        }
        return headers;
    }


}
