package com.jidouauto.lib.rxhelper.backoff;

/**
 * 指数退避
 * 每次重试延迟时间为上一次的‘multiplier’倍，第一次延迟‘baseInterval’ms
 */
public class ExponentialBackOffStrategy implements BackOffStrategy {

    private long baseInterval = 1000;      //默认间隔

    private double multiplier = 1.5f;      //指数

    private long maxInterval = 60 * 1000; //最大间隔

    private double randomRange = 0.0d;      //上下随机浮动范围

    /**
     * @param baseInterval 第一次delay
     * @param multiplier   每次延迟时间为上一次的multiplier倍
     * @param maxInterval  最大延迟时间
     */
    public ExponentialBackOffStrategy(long baseInterval, double multiplier, long maxInterval) {
        this(baseInterval, multiplier, 0.0d, maxInterval);
    }

    /**
     * @param baseInterval 第一次delay
     * @param multiplier   每次延迟时间为上一次的multiplier倍
     * @param randomRange  每次延迟抖动范围，如果当前延迟时间为1000ms，抖动范围为0.2，则实际可能的范围为800~1200
     * @param maxInterval  最大延迟时间
     */
    public ExponentialBackOffStrategy(long baseInterval, double multiplier, double randomRange, long maxInterval) {
        if (baseInterval < 1) {
            throw new IllegalArgumentException("baseInterval cannot less than 1!");
        }

        if (multiplier < 1.0f) {
            throw new IllegalArgumentException("multiplier cannot less than 1.0!");
        }

        if (maxInterval < baseInterval) {
            throw new IllegalArgumentException("maxInterval cannot less than baseInterval!");
        }

        if (randomRange < 0.0d) {
            throw new IllegalArgumentException("randomRange cannot less than 0.0!");
        }

        this.baseInterval = baseInterval;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
        this.randomRange = randomRange;
    }

    @Override
    public long getDelay(int retryCount) {
        long delay = (long) Math.min(baseInterval * Math.pow(multiplier, retryCount - 1), maxInterval);
        if (Double.compare(randomRange, 0.0d) != 0) {
            long low = (long) Math.max(delay - (delay * randomRange), 1);
            long high = (long) Math.min(delay + (delay * randomRange), maxInterval);
            delay = low + (int) (Math.random() * ((high - low) + 1));
        }
        return delay;
    }
}
