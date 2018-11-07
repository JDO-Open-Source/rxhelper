package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.transformer.LifecycleTransformer;

import io.reactivex.Observable;

/**
 * @author eddie
 * <p>
 * 实现该接口需要提供一个观察生命周期的Observable
 * @see {@link LifecycleTransformer}
 */
public interface LifecycleSource<T> {

    Observable<T> getLifecycleObservable();
}
