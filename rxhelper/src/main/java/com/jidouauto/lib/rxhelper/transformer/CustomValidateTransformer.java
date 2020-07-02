package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.DataValidator;

import org.reactivestreams.Publisher;

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

class CustomValidateTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T> {

    private DataValidator<T, ?> dataValidator;

    public CustomValidateTransformer(DataValidator<T, ? extends Exception> dataValidator) {
        this.dataValidator = dataValidator;
    }


    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .map(resp -> {
                    dataValidator.validate(resp);
                    return resp;
                });
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .map(resp -> {
                    dataValidator.validate(resp);
                    return resp;
                });
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .map(resp -> {
                    dataValidator.validate(resp);
                    return resp;
                });
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .map(resp -> {
                    dataValidator.validate(resp);
                    return resp;
                });
    }
}
