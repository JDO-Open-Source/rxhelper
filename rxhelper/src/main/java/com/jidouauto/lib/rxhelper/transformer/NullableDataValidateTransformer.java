package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.NullableData;
import com.jidouauto.lib.rxhelper.Validator;

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

class NullableDataValidateTransformer<T extends NullableData<? extends Validator>> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T> {

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .map(resp -> {
                    if (resp.isNotNull()) {
                        resp.get().validate();
                    }
                    return resp;
                });
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .map(resp -> {
                    if (resp.isNotNull()) {
                        resp.get().validate();
                    }
                    return resp;
                });
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .map(resp -> {
                    if (resp.isNotNull()) {
                        resp.get().validate();
                    }
                    return resp;
                });
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .map(resp -> {
                    if (resp.isNotNull()) {
                        resp.get().validate();
                    }
                    return resp;
                });
    }
}
