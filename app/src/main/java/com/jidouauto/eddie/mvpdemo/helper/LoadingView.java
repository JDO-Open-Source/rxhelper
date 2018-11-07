package com.jidouauto.eddie.mvpdemo.helper;

public interface LoadingView {

    void showLoading(String tag);

    void showLoading(String tag, String message);

    void showLoading(String tag, String title, String message);

    void showLoading(String tag, String title, String message, boolean cancelable);

    void cancel(String tag);

    void cancelAll();
}
