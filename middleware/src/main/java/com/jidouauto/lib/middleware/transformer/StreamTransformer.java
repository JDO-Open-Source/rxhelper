package com.jidouauto.lib.middleware.transformer;

import android.util.Log;

import com.google.gson.JsonParseException;
import com.jidouauto.lib.middleware.DataSource;
import com.jidouauto.lib.middleware.IdentityValidator;
import com.jidouauto.lib.middleware.ResultValidator;
import com.jidouauto.lib.middleware.exception.BaseException;
import com.jidouauto.lib.middleware.exception.DataException;
import com.jidouauto.lib.middleware.exception.NetworkException;
import com.jidouauto.lib.middleware.exception.UnknowException;
import com.jidouauto.lib.middleware.transformer.DataTransformer;
import com.jidouauto.lib.middleware.transformer.IOUITransformer;
import com.jidouauto.lib.middleware.transformer.IdentityTransformer;
import com.jidouauto.lib.middleware.transformer.ResultTransformer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

/**
 * @author eddie
 * <p>
 * 利用Rxjava2 Transformer接口，配合{@link DataSource},{@link IdentityValidator},{@link ResultValidator}
 * 来规范数据处理，进行数据校验，身份校验（Token），错误处理以及失败重试等操作
 */
public class StreamTransformer {

    private static final String TAG = "StreamTransformer";
    /**
     * The constant debug.
     */
    public static boolean debug = false;

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
     * 校验身份错误信息
     *
     * @param <T> 支持身份校验的数据模型必须实现IdentityValidator接口
     * @return observable transformer
     */
    public static <T extends IdentityValidator> ObservableTransformer<T, T> validateIdentity() {
        return new IdentityTransformer<>();
    }

    /**
     * 校验数据错误信息
     *
     * @param <T> 支持数据校验的数据模型必须实现DataValidator接口
     * @return observable transformer
     */
    public static <T extends ResultValidator> ObservableTransformer<T, T> validateResult() {
        return new ResultTransformer<>();
    }

    /**
     * 将data从实现DataSource接口的数据中提取出来
     *
     * @param <T> the type parameter
     * @param <R> the type parameter
     * @return observable transformer
     */
    public static <T extends DataSource<R>, R> ObservableTransformer<T, R> convertToData() {
        return new DataTransformer<>();
    }

    /**
     * 自定义错误类型转换接口
     */
    public interface ErrorConverter {
        /**
         * Convert base exception.
         *
         * @param e the e
         * @return the base exception
         */
        BaseException convert(Throwable e);
    }

    /**
     * 将某个错误类型转换成特定的错误类型方便统一处理
     *
     * @param e 错误类型
     * @return base exception
     */
    public static BaseException convert(Throwable e) {
        if (e instanceof UnknownHostException
                || e instanceof ConnectException
                || e instanceof SocketTimeoutException
                || e instanceof HttpException
                || e instanceof IOException) {
            return new NetworkException(-1, e);
        } else if (e instanceof JsonParseException) {
            return new DataException(-1, e);
        } else if (e instanceof BaseException) {
            return (BaseException) e;
        } else {
            return new UnknowException(UnknowException.UNKNOW_CODE, e);
        }
    }

    /**
     * 异常转换
     *
     * @param <T>            the type parameter
     * @param errorConverter the error converter
     * @return observable transformer
     */
    public static <T> ObservableTransformer<T, T> convertError(ErrorConverter errorConverter) {
        return upstream -> upstream
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends T>>) e -> {
                    return Observable.error(errorConverter.convert(e));
                });
    }

    /**
     * 通用异常转换
     *
     * @param <T> the type parameter
     * @return observable transformer
     */
    public static <T> ObservableTransformer<T, T> convertError() {
        return convertError(e -> convert(e));
    }

    /**
     * The interface Retry on error.
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
    public static <T> ObservableTransformer<T, T> retryWhenError(RetryOnError retryOnError, final int retryCount, final long delayMillisecond, ObservableSource<?> retryAfter) {
        return upstream -> upstream
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    int currentRetry;

                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(throwable -> {
                            if (retryOnError.isRetry(throwable)) {
                                if (debug) {
                                    Log.d(TAG, "retry on :" + throwable.getClass().getSimpleName() + ":" + throwable.getMessage());
                                }
                                //重试，最多重试retryCount次
                                if (currentRetry < retryCount) {
                                    currentRetry++;
                                    //尝试自动登陆
                                    if (debug) {
                                        Log.d(TAG, "第" + currentRetry + "次重试");
                                    }
                                    Observable retryObservable = delayMillisecond > 0 ? Observable.timer(delayMillisecond, TimeUnit.MILLISECONDS) : Observable.just(1);
                                    if (retryAfter == null) {
                                        if (debug) {
                                            Log.d(TAG, "retry after:" + delayMillisecond);
                                        }
                                        return retryObservable;
                                    } else {
                                        if (debug) {
                                            Log.d(TAG, "retryAfter after:" + delayMillisecond);
                                        }
                                        return retryObservable.flatMap(i -> retryAfter);
                                    }
                                }
                            }
                            return Observable.error(throwable);
                        });
                    }
                });
    }

    /**
     * 出现任何错误都重试
     *
     * @param <T>              the type parameter
     * @param retryCount       重试次数
     * @param delayMillisecond 每次重试延迟
     * @return observable transformer
     */
    public static <T> ObservableTransformer<T, T> retryAnyError(final int retryCount, long delayMillisecond) {
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
    public static <T> ObservableTransformer<T, T> retryOnError(final int retryCount, long delayMillisecond, Class<? extends Throwable>... errorClasses) {
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
    public static <T> ObservableTransformer<T, T> retryExceptError(final int retryCount, long delayMillisecond, Class<? extends Throwable>... errorClasses) {
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
    public static <T> ObservableTransformer<T, T> retryWhenError(Class<? extends Throwable> errorType, final int retryCount, long delayMillisecond, ObservableSource<?> retryAfter) {
        return retryWhenError(throwable -> (throwable.getClass().isAssignableFrom(errorType)), retryCount, delayMillisecond, retryAfter);
    }
}
