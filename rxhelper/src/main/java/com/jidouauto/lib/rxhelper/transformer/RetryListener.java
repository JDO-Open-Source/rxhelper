package com.jidouauto.lib.rxhelper.transformer;

public interface RetryListener {

    /**
     * 计划重试
     *
     * @param retryError 造成重试的Error
     * @param retryCount   当前重试次数
     * @param delay        重试之前的Delay
     */
    void scheduleRetry(Throwable retryError, int retryCount, long delay);

    /**
     * 开始重试
     *
     * @param retryError 造成重试的Error
     * @param retryCount   当前重试次数
     */
    void startRetry(Throwable retryError, int retryCount);

}
