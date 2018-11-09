package com.jidouauto.lib.middleware.transformer;

import com.jidouauto.lib.middleware.DataConverter;
import com.jidouauto.lib.middleware.ErrorConverter;
import com.jidouauto.lib.middleware.NullableData;
import com.jidouauto.lib.middleware.RetryOnError;
import com.jidouauto.lib.middleware.Validator;

import io.reactivex.ObservableTransformer;
import io.reactivex.Single;

/**
 * @author eddie
 * <p>
 * 利用Rxjava2 Transformer接口，配合{@link DataConverter},{@link Validator}
 * 来规范数据处理，进行数据校验，身份校验（Token），错误处理以及失败重试等操作
 */
public class StreamTransformer {

    /**
     * 经典的IO-UI的线程切换模型
     *
     * @param <T> the type parameter
     * @return observable transformer
     */
    public static <T> IOUITransformer<T> applyIOUI() {
        return new IOUITransformer<>();
    }

    /**
     * 校验数据错误信息
     *
     * @param <T> 支持数据校验的数据模型必须实现DataValidator接口
     * @return observable transformer
     */
    public static <T extends Validator<? extends Exception>> ValidateTransformer<T> validate() {
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
     *
     * @param <T>              the type parameter
     * @param retryOnError     判断是否重试
     * @param retryCount       重试次数
     * @param delayMillisecond 重试延迟时间
     * @param retryAfter       重试前执行的操作
     * @return observable transformer
     */
    public static <T> RetryWhenTransformer<T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, Single<?> retryAfter) {
        return new RetryWhenTransformer<>(retryOnError, retryCount, delayMillisecond, retryAfter);
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
        return retryWhenError(throwable -> true, retryCount, delayMillisecond, null);
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
        return retryWhenError(throwable -> {
            if (errorClasses == null) {
                return false;
            } else {
                for (Class<? extends Throwable> eClass : errorClasses) {
                    if (throwable.getClass().isAssignableFrom(eClass)) {
                        return true;
                    }
                }

                return false;
            }
        }, retryCount, delayMillisecond, null);
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
        return retryWhenError(throwable -> {
            if (errorClasses == null) {
                return true;
            } else {
                for (Class<? extends Throwable> eClass : errorClasses) {
                    if (throwable.getClass().isAssignableFrom(eClass)) {
                        return false;
                    }
                }

                return true;
            }
        }, retryCount, delayMillisecond, null);
    }


    /**
     * 针对某个错误重试，并且每次重试都在执行完retryAfter之后
     *
     * @param <T>              the type parameter
     * @param errorType        错误类型
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试前延迟
     * @param retryAfter       重试前执行的逻辑
     * @return observable transformer
     */
    public static <T> RetryWhenTransformer<T> retryWhenError(Class<? extends Throwable> errorType, final int retryCount, long delayMillisecond, Single<?> retryAfter) {
        return retryWhenError(throwable -> (throwable.getClass().isAssignableFrom(errorType)), retryCount, delayMillisecond, retryAfter);
    }
}
