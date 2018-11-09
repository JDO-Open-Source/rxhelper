package com.jidouauto.eddie.mvpdemo;

import com.jidouauto.lib.rxhelper.LifecycleSource;
import com.jidouauto.lib.rxhelper.transformer.LifecycleTransformer;

/**
 * @author eddie
 * <p>
 * 对应Activity的生命周期
 * @see {@link LifecycleSource}
 * @see {@link LifecycleTransformer}
 */
public enum LifecycleEvent {
    ON_CREATE,
    ON_START,
    ON_RESUME,
    ON_PAUSE,
    ON_STOP,
    ON_DESTROY,
}