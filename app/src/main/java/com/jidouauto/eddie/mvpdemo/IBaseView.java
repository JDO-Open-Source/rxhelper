package com.jidouauto.eddie.mvpdemo;

import com.jidouauto.lib.middleware.LifecycleSource;

public interface IBaseView<T> extends LifecycleSource<LifecycleEvent> {
    void setPresenter(T presenter);

}