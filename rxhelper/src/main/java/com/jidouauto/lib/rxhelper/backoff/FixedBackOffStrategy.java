package com.jidouauto.lib.rxhelper.backoff;

/**
 * 固定的退避策略，每次重试Delay的时间固定
 */
public class FixedBackOffStrategy implements BackOffStrategy {
    private final long mDelay;

    private double randomRange = 0.0d;      //上下随机浮动范围

    public FixedBackOffStrategy(long delay) {
        this(delay, 0.0d);
    }

    public FixedBackOffStrategy(long delay, double randomRange) {

        if (delay < 0) {
            throw new IllegalArgumentException("delay cannot less than 0!");
        }

        if (randomRange < 0.0d) {
            throw new IllegalArgumentException("randomRange cannot less than 0.0!");
        }
        this.mDelay = delay;
        this.randomRange = randomRange;

    }

    @Override
    public long getDelay(int retryCount) {

        if (Double.compare(randomRange, 0.0d) != 0) {
            long low = (long) (mDelay - (mDelay * randomRange));
            long high = (long) (mDelay + (mDelay * randomRange));
            return low + (int) (Math.random() * ((high - low) + 1));
        }

        return mDelay;
    }
}
