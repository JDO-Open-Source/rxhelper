package com.jidouauto.eddie.mvpdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jidouauto.eddie.mvpdemo.helper.ErrorHandler;
import com.jidouauto.eddie.mvpdemo.helper.LifecycleEventSource;
import com.jidouauto.eddie.mvpdemo.helper.LoadingView;
import com.jidouauto.eddie.mvpdemo.helper.ProgressLoadingView;
import com.jidouauto.eddie.mvpdemo.helper.ToastErrorView;
import com.jidouauto.lib.middleware.LifecycleSource;

import io.reactivex.Observable;

public class BaseActivity extends AppCompatActivity implements LifecycleSource<LifecycleEvent> {
    protected ErrorHandler errorHandler;
    protected LoadingView loadingView;
    protected LifecycleSource lifecycleSource;

    @Override
    public Observable<LifecycleEvent> getLifecycleObservable() {
        return lifecycleSource.getLifecycleObservable();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        errorHandler = new ErrorHandler(new ToastErrorView(this));
        loadingView = new ProgressLoadingView(this);
        lifecycleSource = new LifecycleEventSource(getLifecycle());
    }

    protected ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    protected LoadingView getLoadingView() {
        return loadingView;
    }
}
