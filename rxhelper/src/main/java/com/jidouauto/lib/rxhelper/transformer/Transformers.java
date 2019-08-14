package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.DataConverter;
import com.jidouauto.lib.rxhelper.ErrorConverter;
import com.jidouauto.lib.rxhelper.NullableData;
import com.jidouauto.lib.rxhelper.RetryOnError;
import com.jidouauto.lib.rxhelper.Validator;

import org.reactivestreams.Publisher;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleSource;
import io.reactivex.internal.operators.single.SingleToObservable;

/**
 * @author eddie
 * <p>
 * 利用Rxjava2 Transformer接口，配合{@link DataConverter},{@link Validator}
 * 来规范数据处理，进行数据校验，身份校验（Token），错误处理以及失败重试等操作
 */
public class Transformers {

    /**
     * 校验数据错误信息
     *
     * @param <T> 支持数据校验的数据模型必须实现DataValidator接口
     * @return observable transformer
     */
    public static <T extends Validator<?>> ValidateTransformer<T> validate() {
        return new ValidateTransformer<>();
    }

    /**
     * 如果传递下来的数据是NullableData类型，并且NullableData中的value类型为Validator的子类，并且该value不为null，那么则调用该value的validate方法。
     *
     * @param <T>
     * @return
     */
    public static <T extends NullableData<? extends Validator>> ObservableTransformer<T, T> validateNullable() {
        return new NullableDataValidateTransformer<>();
    }


    /**
     * 将data从实现DataConverter接口的数据中提取出来
     *
     * @param <T> the type parameter
     * @param <R> the type parameter
     * @return observable transformer
     */
    public static <T extends DataConverter<R>, R> DataConvertTransformer<T, R> convertToData() {
        return new DataConvertTransformer<>();
    }

    /**
     * 将data从实现DataConverter接口的数据中提取出来，如果这个数据可能为空，那么可以提供一个默认值
     *
     * @param defaultValue data为空的情况返回该默认值
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T extends DataConverter<NullableData<R>>, R> NullableDataConvertTransformer<T, R> convertToData(R defaultValue) {
        return new NullableDataConvertTransformer<>(defaultValue);
    }

    /**
     * 异常转换
     *
     * @param <T>            the type parameter
     * @param errorConverter the error converter
     * @return observable transformer
     */
    public static <T> ConvertErrorTransformer<T> convertError(ErrorConverter errorConverter) {
        return new ConvertErrorTransformer<>(errorConverter);
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
    public static <T> RetryWhenTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter) {
        return new RetryWhenTransformer<>(retryOnError, retryCount, delayMillisecond, retryUntil, retryAfter);
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
    public static <T> RetryWhenTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond) {
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
    public static <T> RetryWhenTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, ObservableSource<?> retryAfter) {
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
    public static <T> RetryWhenTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, SingleSource<?> retryAfter) {
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
    public static <T> RetryWhenTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, Publisher<?> retryAfter) {
        return retryWhenError(retryOnError, retryCount, delayMillisecond, null, Observable.fromPublisher(retryAfter));
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
    public static <T> RetryWhenTransformer<T> retryAnyError(final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter) {
        return retryWhenError(throwable -> true, retryCount, delayMillisecond, retryUntil, retryAfter);
    }

    /**
     * 出现任何错误都重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟
     * @return observable transformer
     */
    public static <T> RetryWhenTransformer<T> retryAnyError(final int retryCount, long delayMillisecond) {
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
    public static <T> RetryWhenTransformer<T> retryAnyError(final int retryCount, long delayMillisecond, SingleSource<?> retryAfter) {
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
    public static <T> RetryWhenTransformer<T> retryAnyError(final int retryCount, long delayMillisecond, Publisher<?> retryAfter) {
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
    public static <T> RetryWhenTransformer<T> retryAnyError(final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter) {
        return retryAnyError(retryCount, delayMillisecond, null, retryAfter);
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
    public static <T> RetryWhenTransformer<T> retryOnError(final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
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
        }, retryCount, delayMillisecond, retryUntil, retryAfter);
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
    public static <T> RetryWhenTransformer<T> retryOnError(final int retryCount, long delayMillisecond, Class<? extends Throwable>... errorClasses) {
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
    public static <T> RetryWhenTransformer<T> retryOnError(final int retryCount, long delayMillisecond, SingleSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
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
    public static <T> RetryWhenTransformer<T> retryOnError(final int retryCount, long delayMillisecond, Publisher<?> retryAfter, Class<? extends Throwable>... errorClasses) {
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
    public static <T> RetryWhenTransformer<T> retryOnError(final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryOnError(retryCount, delayMillisecond, null, retryAfter, errorClasses);
    }

    /**
     * @param retryCount       重试次数
     * @param delayMillisecond 重试前延迟
     * @param retryUntil       一直重试直到retryUntil发射第一个数据或终止
     * @param retryAfter       每次重试前都需要执行的操作
     * @param errorClasses     如果错误类型包含在errorClasses才重试
     * @param <T>
     * @return
     */
    public static <T> RetryWhenTransformer<T> retryExceptError(final int retryCount, final long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
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
        }, retryCount, delayMillisecond, retryUntil, retryAfter);
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
    public static <T> RetryWhenTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, Class<? extends Throwable>... errorClasses) {
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
    public static <T> RetryWhenTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, SingleSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
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
    public static <T> RetryWhenTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, Publisher<?> retryAfter, Class<? extends Throwable>... errorClasses) {
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
    public static <T> RetryWhenTransformer<T> retryExceptError(final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter, Class<? extends Throwable>... errorClasses) {
        return retryExceptError(retryCount, delayMillisecond, null, retryAfter, errorClasses);
    }

}
