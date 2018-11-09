package com.jidouauto.eddie.mvpdemo;

import com.jidouauto.lib.base.utils.LogUtils;
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
            LogUtils.w(TAG, "LifecycleSource is NULL,can not ");
        }

        if (mLifecycleSource.getLifecycleObservable() == null) {
            LogUtils.w(TAG, "LifecycleSource.getLifecycleObservable() is NULL");
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
