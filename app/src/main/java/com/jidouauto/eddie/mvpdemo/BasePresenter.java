package com.jidouauto.eddie.mvpdemo;

import com.jidouauto.lib.rxhelper.LifecycleSource;
import com.jidouauto.lib.rxhelper.transformer.LifecycleTransformer;

public class BasePresenter implements IBasePresenter {

    public static final String TAG = "BasePresenter";

    LifecycleSource mLifecycleSource;

    public BasePresenter(LifecycleSource lifecycleSource) {
        mLifecycleSource = lifecycleSource;
    }

    public <T, R> LifecycleTransformer<T> bindUntilEvent(R... events) {
        if (mLifecycleSource == null) {
            throw new NullPointerException("LifecycleSource is NULL");
        }

        if (mLifecycleSource.getLifecycleObservable() == null) {
            throw new NullPointerException("LifecycleSource.getLifecycleObservable() is NULL");
        }
        return LifecycleTransformer.bindUntilEvent(mLifecycleSource.getLifecycleObservable(), events);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

}
