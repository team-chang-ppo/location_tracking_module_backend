package org.changppo.monioring.server.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterConfigurationProperties {
    private Map<String, Capacity> capacities = Map.of(
            "GRADE_FREE", new Capacity(50, 100),
            "GRADE_CLASSIC", new Capacity(100, 500),
            "GRADE_PREMIUM", new Capacity(200, 1000)
    );

    public Map<String, Capacity> getCapacities() {
        return capacities;
    }

    public void setCapacities(Map<String, Capacity> capacities) {
        this.capacities = capacities;
    }

    public static class Capacity {
        private long replenishRate;
        private long burstCapacity;

        protected Capacity() {
        }

        public Capacity(long replenishRate, long burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }

        public long getReplenishRate() {
            return replenishRate;
        }

        public void setReplenishRate(long replenishRate) {
            this.replenishRate = replenishRate;
        }

        public long getBurstCapacity() {
            return burstCapacity;
        }

        public void setBurstCapacity(long burstCapacity) {
            this.burstCapacity = burstCapacity;
        }
    }
}
