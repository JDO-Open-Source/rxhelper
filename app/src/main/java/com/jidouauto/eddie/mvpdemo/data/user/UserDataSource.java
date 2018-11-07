package com.jidouauto.eddie.mvpdemo.data.user;

import com.jidouauto.eddie.mvpdemo.bean.ResultResp;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

import io.reactivex.Observable;

public interface UserDataSource {
    Observable<ResultResp<LoginInfo>> login(String username, String password);

    Observable<ResultResp<String>> getToken();

    void expireToken();

    Observable<ResultResp<UserInfo>> getUserInfo(String token);

    String getLocalToken();
}