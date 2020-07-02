package com.jidouauto.lib.rxhelper.backoff;

/**
 * 退避策略
 */
public interface BackOffStrategy {
    long getDelay(int retryCount);
}
