package com.jidouauto.lib.rxhelper;

import com.jidouauto.lib.rxhelper.transformer.RetryTransformers;

import io.reactivex.SingleSource;

/**
 * @author eddie
 * <p>
 * 根据错误类型判断是否应该重试
 * @see {@link RetryTransformers#retryWhenError(RetryOnError, int, long, SingleSource)}
 */
public interface RetryOnError {
    /**
     * 根据错误类型判断是否应该重试
     *
     * @param throwable the throwable
     * @return 是否重试 boolean
     */
    boolean isRetry(Throwable throwable);
}