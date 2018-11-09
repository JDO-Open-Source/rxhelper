package com.jidouauto.lib.rxhelper;

import com.jidouauto.lib.rxhelper.transformer.LifecycleTransformer;

import io.reactivex.Observable;

/**
 * @author eddie
 * <p>
 * 实现该接口需要提供一个观察生命周期的Observable
 * @see {@link LifecycleTransformer}
 */
public interface LifecycleSource<T> {

    /**
     * @return 一个观察生命周期的Observable
     */
    Observable<T> getLifecycleObservable();
}
