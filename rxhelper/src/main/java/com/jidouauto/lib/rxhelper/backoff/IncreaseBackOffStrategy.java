package com.jidouauto.lib.rxhelper.backoff;

/**
 * 增量退避
 * <p>
 * baseInterval = 1000，increase = 500
 * <p>
 * 1000 1500 2000 2500 ...
 */
public class IncreaseBackOffStrategy implements BackOffStrategy {

    private long firstInterval = 1000;      //首次间隔
    private long increase = 1000;           //间隔增量

    private long maxInterval = 60 * 1000;   //最大间隔

    private double randomRange = 0.0d;      //上下随机浮动范围

    /**
     * @param firstInterval 首次延迟时间
     * @param increase      延迟增量
     * @param maxInterval   最大延迟时间
     */
    public IncreaseBackOffStrategy(long firstInterval, long increase, long maxInterval) {
        this(firstInterval, increase, 0.0d, maxInterval);
    }

    /**
     * @param firstInterval 首次延迟时间
     * @param increase      延迟增量
     * @param randomRange   每次延迟抖动范围，如果当前延迟时间为1000ms，抖动范围为0.2，则实际可能的范围为800~1200
     * @param maxInterval   最大延迟时间
     */
    public IncreaseBackOffStrategy(long firstInterval, long increase, double randomRange, long maxInterval) {
        if (firstInterval < 0) {
            throw new IllegalArgumentException("firstInterval cannot less than 0!");
        }

        if (increase < 0) {
            throw new IllegalArgumentException("increase cannot less than 0!");
        }


        if (maxInterval < firstInterval) {
            throw new IllegalArgumentException("maxInterval cannot less than baseInterval!");
        }

        if (randomRange < 0.0d) {
            throw new IllegalArgumentException("randomRange cannot less than 0.0!");
        }

        this.firstInterval = firstInterval;
        this.increase = increase;
        this.maxInterval = maxInterval;
        this.randomRange = randomRange;
    }

    @Override
    public long getDelay(int retryCount) {
        long delay = Math.min(firstInterval + increase * (retryCount - 1), maxInterval);
        if (Double.compare(randomRange, 0.0d) != 0) {
            long low = (long) Math.max(delay - (delay * randomRange), 1);
            long high = (long) Math.min(delay + (delay * randomRange), maxInterval);
            delay = low + (int) (Math.random() * ((high - low) + 1));
        }
        return delay;
    }
}
