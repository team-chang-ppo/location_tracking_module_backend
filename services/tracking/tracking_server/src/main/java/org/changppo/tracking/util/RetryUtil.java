package org.changppo.tracking.util;

import java.util.concurrent.ThreadLocalRandom;

public class RetryUtil {
    public static final int MAX_ATTEMPTS = 5;
    private static final long INITIAL_DELAY = 1000L;
    private static final long MAX_DELAY = 30000L;

    public static long calculateDelay(int attempt) {
        long delay = INITIAL_DELAY * (long) Math.pow(2, attempt);
        long jitter = ThreadLocalRandom.current().nextLong(delay / 2);
        delay += jitter;
        return Math.min(delay, MAX_DELAY);
    }
}
