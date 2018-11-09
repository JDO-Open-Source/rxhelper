package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.DataConverter;

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

class DataConvertTransformer<T extends DataConverter<R>, R> implements ObservableTransformer<T, R>,
        FlowableTransformer<T, R>,
        SingleTransformer<T, R>,
        MaybeTransformer<T, R> {

    @Override
    public Publisher<R> apply(Flowable<T> upstream) {
        return upstream
                .map(resp -> resp.convert());
    }

    @Override
    public MaybeSource<R> apply(Maybe<T> upstream) {
        return upstream
                .map(resp -> resp.convert());
    }

    @Override
    public ObservableSource<R> apply(Observable<T> upstream) {
        return upstream
                .map(resp -> resp.convert());
    }

    @Override
    public SingleSource<R> apply(Single<T> upstream) {
        return upstream
                .map(resp -> resp.convert());
    }
}
