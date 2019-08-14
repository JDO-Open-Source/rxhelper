package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.RetryOnError;
import com.jidouauto.lib.rxhelper.ext.ObservableFirstSingle;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

class RetryWhenTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T> {

    private RetryOnError mRetryOnError;
    private long mDelayMillisecond;
    private int mRetryCount;
    int currentRetry;
    private ObservableSource<?> mRetryAfter;
    private ObservableSource<?> mRetryUntil;

    public RetryWhenTransformer(RetryOnError retryOnError, int retryCount, long delayMillisecond, ObservableSource<?> retryUntil, ObservableSource<?> retryAfter) {
        if (retryCount < 0 && retryUntil == null) {
            throw new IllegalArgumentException("you must set retry count >=0 otherwise retryUntil retryUntil can't be null!");
        }
        System.out.println("RetryWhenTransformer() called with: retryOnError = [" + retryOnError + "], retryCount = [" + retryCount + "], delayMillisecond = [" + delayMillisecond + "]");
        mRetryOnError = retryOnError;
        mRetryCount = retryCount;
        mDelayMillisecond = delayMillisecond;
        mRetryUntil = retryUntil;
        mRetryAfter = retryAfter;
    }

    private boolean retry() {
        if (mRetryCount < 0) {
            return true;
        }
        boolean retry = currentRetry < mRetryCount;
        if (retry) {
            currentRetry++;
        }
        return retry;
    }

    public Function<Flowable<Throwable>, Publisher<?>> retryFlowableFunction() {
        return new Function<Flowable<Throwable>, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
                return throwableFlowable.flatMap(throwable -> {
                    if (mRetryOnError.isRetry(throwable)) {
                        //重试，最多重试retryCount次
                        if (retry()) {
                            System.out.println("retry onError [" + throwable.getClass() + "][" + mRetryCount + "]");
                            //尝试自动登陆
                            if (mRetryAfter == null) {
                                return Flowable.timer(mDelayMillisecond, TimeUnit.MILLISECONDS);
                            } else {
                                if (mRetryUntil == null) {
                                    return Flowable.timer(mDelayMillisecond, TimeUnit.MILLISECONDS)
                                            .flatMap(i -> new ObservableFirstSingle<>(mRetryAfter).toFlowable());
                                } else {
                                    return Flowable.timer(mDelayMillisecond, TimeUnit.MILLISECONDS)
                                            .flatMap(i -> new ObservableFirstSingle<>(mRetryAfter).toFlowable())
                                            .takeUntil(new ObservableFirstSingle<>(mRetryUntil)
                                                    .toFlowable()
                                                    .flatMap(new Function<Object, Publisher<?>>() {
                                                        @Override
                                                        public Publisher<?> apply(Object o) throws Exception {
                                                            return Flowable.error(throwable);
                                                        }
                                                    }));
                                }
                            }
                        }
                    }
                    return Flowable.error(throwable);
                });
            }
        };
    }

    public Function<Observable<Throwable>, ObservableSource<?>> retryObservableFuncation() {
        return new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(throwable -> {
                    if (mRetryOnError.isRetry(throwable)) {
                        //重试，最多重试retryCount次
                        if (retry()) {
                            System.out.println("retry onError [" + throwable.getClass() + "][" + mRetryCount + "]");
                            //尝试自动登陆
                            if (mRetryAfter == null) {
                                return Observable.timer(mDelayMillisecond, TimeUnit.MILLISECONDS);
                            } else {
                                if (mRetryUntil == null) {
                                    return Observable
                                            .timer(mDelayMillisecond, TimeUnit.MILLISECONDS)
                                            .flatMap(i -> new ObservableFirstSingle<>(mRetryAfter).toObservable());
                                } else {
                                    return Observable
                                            .timer(mDelayMillisecond, TimeUnit.MILLISECONDS)
                                            .flatMap(i -> new ObservableFirstSingle<>(mRetryAfter).toObservable())
                                            .takeUntil(new ObservableFirstSingle<>(mRetryUntil)
                                                    .toObservable()
                                                    .flatMap(new Function<Object, ObservableSource<?>>() {
                                                        @Override
                                                        public ObservableSource<?> apply(Object o) throws Exception {
                                                            return Observable.error(throwable);
                                                        }
                                                    })
                                            );
                                }
                            }
                        }
                    }
                    return Observable.error(throwable);
                });
            }
        };
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .retryWhen(retryFlowableFunction());
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .retryWhen(retryFlowableFunction());
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .retryWhen(retryObservableFuncation());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .retryWhen(retryFlowableFunction());
    }
}
