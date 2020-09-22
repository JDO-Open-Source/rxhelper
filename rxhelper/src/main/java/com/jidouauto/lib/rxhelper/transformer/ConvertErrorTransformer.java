package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.ErrorConverter;

import org.reactivestreams.Publisher;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
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

class ConvertErrorTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        CompletableTransformer,
        MaybeTransformer<T, T> {

    private ErrorConverter<? extends Exception> errorConverter;

    public ConvertErrorTransformer(ErrorConverter<? extends Exception> errorConverter) {
        this.errorConverter = errorConverter;
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .onErrorResumeNext(throwable -> {
                    return Flowable.error(errorConverter.convert(throwable));
                });
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .onErrorResumeNext(throwable -> {
                    return Maybe.error(errorConverter.convert(throwable));
                });
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .onErrorResumeNext(throwable -> {
                    return Observable.error(errorConverter.convert(throwable));
                });
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream
                .onErrorResumeNext(throwable -> {
                    return Completable.error(errorConverter.convert(throwable));
                });
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .onErrorResumeNext(throwable -> Single.error(errorConverter.convert(throwable)));
    }
}
