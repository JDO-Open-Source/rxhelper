package com.jidouauto.eddie.mvpdemo.helper;

public interface ErrorView {

    void onNetworkError();

    void onMessageError(String message);

}
