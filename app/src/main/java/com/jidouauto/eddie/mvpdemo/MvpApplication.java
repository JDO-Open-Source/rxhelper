package com.jidouauto.eddie.mvpdemo;

import android.app.Application;

public class MvpApplication extends Application {

    private static MvpApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        MvpApplication.application = this;
    }

    public static MvpApplication getInstance(){
        return application;
    }
}
