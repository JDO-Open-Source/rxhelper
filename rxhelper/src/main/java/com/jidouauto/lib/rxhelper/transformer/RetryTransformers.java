package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.RetryOnError;
import com.jidouauto.lib.rxhelper.backoff.BackOffStrategy;
import com.jidouauto.lib.rxhelper.backoff.FixedBackOffStrategy;

import org.reactivestreams.Publisher;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleSource;
import io.reactivex.internal.operators.single.SingleToObservable;

/**
 * @author eddie
 * <p>
 * 提供一系列重试的方法，并支持不同的重试策略。
 * 1：可以根据指定的错误重试
 * 2：支持设置重试次数，并且每次重试前可以添加delay
 * 3：可以在执行某些任务后再重试
 * 4：提供常见的退避策略并支持自定义退避策略
 * 5：在设置最大重试次数的情况下可以添加拦截器终止重试
 */
public class RetryTransformers {

    /**
     * 通过RetryOnError接口来决定是否重试
     * 如果 retryCount >0 和 retryUntil != null 那么一直重试直到retryUntil发射第一个数据或终止,或者达到最大次数，先到为准
     *
     * @param retryOnError    判断是否重试
     * @param retryCount      重试次数
     * @param backOffStrategy 退避策略
     * @param retryUntil      一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter      每次重试前执行的操作
     * @param retryListener   重试监听
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final BackOffStrategy backOffStrategy, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, RetryListener retryListener) {
        return new RetryTransformer<>(retryOnError, retryCount, backOffStrategy, retryUntil, retryAfter, retryListener);
    }

    /**
     * 通过RetryOnError接口来决定是否重试
     * 如果 retryCount >0 和 retryUntil != null 那么一直重试直到retryUntil发射第一个数据或终止,或者达到最大次数，先到为准
     *
     * @param retryOnError     判断是否重试
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟时间
     * @param retryUntil       一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter       每次重试前执行的操作
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter) {
        return retryWhenError(retryOnError, retryCount, new FixedBackOffStrategy(delayMillisecond), retryUntil, retryAfter, null);
    }

    /**
     * 通过RetryOnError接口来决定是否重试
     *
     * @param <T>              the type parameter
     * @param retryOnError     判断是否重试
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟时间
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond) {
        return retryWhenError(retryOnError, retryCount, delayMillisecond, null, null);
    }

    /**
     * 通过RetryOnError接口来决定是否重试
     *
     * @param <T>              the type parameter
     * @param retryOnError     判断是否重试
     * @param retryCount       重试次数
     * @param delayMillisecond 重试延迟时间
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, ObservableSource<?> retryAfter) {
        return retryWhenError(retryOnError, retryCount, delayMillisecond, null, retryAfter);
    }

    /**
     * 通过RetryOnError接口来决定是否重试
     *
     * @param <T>              the type parameter
     * @param retryOnError     判断是否重试
     * @param retryCount       重试次数
     * @param delayMillisecond 重试延迟时间
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, SingleSource<?> retryAfter) {
        return retryWhenError(retryOnError, retryCount, delayMillisecond, null, new SingleToObservable<>(retryAfter));
    }

    /**
     * 通过RetryOnError接口来决定是否重试
     *
     * @param <T>              the type parameter
     * @param retryOnError     判断是否重试
     * @param retryCount       重试次数
     * @param delayMillisecond 重试延迟时间
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, Publisher<?> retryAfter) {
        return retryWhenError(retryOnError, retryCount, delayMillisecond, null, Observable.fromPublisher(retryAfter));
    }

    /**
     * 出现任何错误都重试
     *
     * @param retryCount      重试次数
     * @param backOffStrategy 退避策略
     * @param retryUntil      一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter      重试前执行的操作
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryAnyError(final int retryCount, final BackOffStrategy backOffStrategy, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, RetryListener retryListener) {
        return retryWhenError(throwable -> true, retryCount, backOffStrategy, retryUntil, retryAfter, retryListener);
    }

    /**
     * 出现任何错误都重试
     *
     * @param retryCount       重试次数
     * @param delayMillisecond 重试延迟时间
     * @param retryUntil       一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter       重试前执行的操作
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryAnyError(final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter) {
        return retryAnyError(retryCount, new FixedBackOffStrategy(delayMillisecond), retryUntil, retryAfter, null);
    }

    /**
     * 出现任何错误都重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryAnyError(final int retryCount, long delayMillisecond) {
        return retryAnyError(retryCount, delayMillisecond, null, null);
    }

    /**
     * 出现任何错误都重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryAnyError(final int retryCount, long delayMillisecond, SingleSource<?> retryAfter) {
        return retryAnyError(retryCount, delayMillisecond, null, new SingleToObservable<>(retryAfter));
    }

    /**
     * 出现任何错误都重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryAnyError(final int retryCount, long delayMillisecond, Publisher<?> retryAfter) {
        return retryAnyError(retryCount, delayMillisecond, null, Observable.fromPublisher(retryAfter));
    }

    /**
     * 出现任何错误都重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryAnyError(final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter) {
        return retryAnyError(retryCount, delayMillisecond, null, retryAfter);
    }

    /**
     * 出现指定类型的错误才重试
     *
     * @param retryCount      重试次数
     * @param backOffStrategy 退避策略
     * @param retryUntil      一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter      每次重试前执行的操作
     * @param errorClasses    如果错误类型包含在errorClasses才重试
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryOnError(final int retryCount, final BackOffStrategy backOffStrategy, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, RetryListener retryListener, Class<? extends Throwable>... errorClasses) {
        return retryWhenError(throwable -> {
            if (errorClasses == null) {
                return false;
            } else {
                for (Class<? extends Throwable> eClass : errorClasses) {
                    if (eClass.isAssignableFrom(throwable.getClass())) {
                        return true;
                    }
                }

                return false;
            }
        }, retryCount, backOffStrategy, retryUntil, retryAfter, retryListener);
    }

    /**
     * 出现指定类型的错误才重试
     *
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryUntil       一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter       每次重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryOnError(final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryOnError(retryCount, new FixedBackOffStrategy(delayMillisecond), retryUntil, retryAfter, null, errorClasses);
    }

    /**
     * 出现指定类型的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryOnError(final int retryCount, long delayMillisecond, Class<? extends Throwable>... errorClasses) {
        return retryOnError(retryCount, delayMillisecond, null, null, errorClasses);
    }

    /**
     * 出现指定类型的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryAfter       重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryOnError(final int retryCount, long delayMillisecond, SingleSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryOnError(retryCount, delayMillisecond, null, new SingleToObservable<>(retryAfter), errorClasses);
    }

    /**
     * 出现指定类型的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryAfter       重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryOnError(final int retryCount, long delayMillisecond, Publisher<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryOnError(retryCount, delayMillisecond, null, Observable.fromPublisher(retryAfter), errorClasses);
    }

    /**
     * 出现指定类型的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryAfter       重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryOnError(final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryOnError(retryCount, delayMillisecond, null, retryAfter, errorClasses);
    }

    /**
     * 出现非指定类型的错误才重试
     *
     * @param retryCount      重试次数
     * @param backOffStrategy 退避策略
     * @param retryUntil      一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter      每次重试前都需要执行的操作
     * @param errorClasses    如果错误类型包含在errorClasses才重试
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryExceptError(final int retryCount, final BackOffStrategy backOffStrategy, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, RetryListener retryListener, Class<? extends Throwable>... errorClasses) {
        return retryWhenError(throwable -> {
            if (errorClasses == null) {
                return true;
            } else {
                for (Class<? extends Throwable> eClass : errorClasses) {
                    if (eClass.isAssignableFrom(throwable.getClass())) {
                        return false;
                    }
                }

                return true;
            }
        }, retryCount, backOffStrategy, retryUntil, retryAfter, retryListener);
    }

    /**
     * 出现非指定类型的错误才重试
     *
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryUntil       一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter       每次重试前都需要执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @param <T>
     * @return
     */
    public static <T> RetryTransformer<T> retryExceptError(final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryExceptError(retryCount, new FixedBackOffStrategy(delayMillisecond), retryUntil, retryAfter, null, errorClasses);
    }

    /**
     * 出现非指定的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param errorClasses     如果错误类型包含在errorClasses则不重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, Class<? extends Throwable>... errorClasses) {
        return retryExceptError(retryCount, delayMillisecond, null, null, errorClasses);
    }

    /**
     * 出现非指定的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryAfter       重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses则不重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, SingleSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryExceptError(retryCount, delayMillisecond, null, new SingleToObservable<>(retryAfter), errorClasses);
    }

    /**
     * 出现非指定的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryAfter       重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses则不重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, Publisher<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryExceptError(retryCount, delayMillisecond, Observable.fromPublisher(retryAfter), null, errorClasses);
    }

    /**
     * 出现非指定的错误才重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryAfter       重试前执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses则不重试
     * @return observable transformer
     */
    public static <T> RetryTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryExceptError(retryCount, delayMillisecond, null, retryAfter, errorClasses);
    }

}
