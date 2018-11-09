package com.jidouauto.eddie.mvpdemo.helper;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.lib.rxhelper.LifecycleSource;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class LifecycleEventSource implements LifecycleObserver, LifecycleSource<LifecycleEvent> {

    BehaviorSubject<LifecycleEvent> lifecycleSubject = BehaviorSubject.create();

    public LifecycleEventSource(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @Override
    public Observable<LifecycleEvent> getLifecycleObservable() {
        return lifecycleSubject.hide();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        onLifecycleEvent(LifecycleEvent.ON_CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(LifecycleOwner owner) {
        onLifecycleEvent(LifecycleEvent.ON_START);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(LifecycleOwner owner) {
        onLifecycleEvent(LifecycleEvent.ON_RESUME);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner) {
        onLifecycleEvent(LifecycleEvent.ON_PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(LifecycleOwner owner) {
        onLifecycleEvent(LifecycleEvent.ON_STOP);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        onLifecycleEvent(LifecycleEvent.ON_DESTROY);
        owner.getLifecycle().removeObserver(this);
    }

    public void onLifecycleEvent(LifecycleEvent event) {
        lifecycleSubject.onNext(event);
    }

}
