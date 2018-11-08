package com.jidouauto.lib.middleware.transformer;

import com.jidouauto.lib.middleware.DataConverter;
import com.jidouauto.lib.middleware.NullableData;

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

class NullableDataConvertTransformer<T extends DataConverter<NullableData<R>>, R> implements ObservableTransformer<T, R>,
        FlowableTransformer<T, R>,
        SingleTransformer<T, R>,
        MaybeTransformer<T, R> {

    private R defaultValue;

    public NullableDataConvertTransformer(R defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Publisher<R> apply(Flowable<T> upstream) {
        return upstream
                .map(resp -> {
                    NullableData<R> data = resp.convert();
                    if (data.isNull()) {
                        return defaultValue;
                    }
                    return data.get();
                });
    }

    @Override
    public MaybeSource<R> apply(Maybe<T> upstream) {
        return upstream
                .map(resp -> {
                    NullableData<R> data = resp.convert();
                    if (data.isNull()) {
                        return defaultValue;
                    }
                    return data.get();
                });
    }

    @Override
    public ObservableSource<R> apply(Observable<T> upstream) {
        return upstream
                .map(resp -> {
                    NullableData<R> data = resp.convert();
                    if (data.isNull()) {
                        return defaultValue;
                    }
                    return data.get();
                });
    }

    @Override
    public SingleSource<R> apply(Single<T> upstream) {
        return upstream
                .map(resp -> {
                    NullableData<R> data = resp.convert();
                    if (data.isNull()) {
                        return defaultValue;
                    }
                    return data.get();
                });
    }
}
