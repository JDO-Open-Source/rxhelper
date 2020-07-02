package com.jidouauto.eddie.mvpdemo.api;

import com.jidouauto.eddie.mvpdemo.bean.DataResp;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

import io.reactivex.Single;

public interface ApiService {

    Single<DataResp<LoginInfo>> login(String userName, String password);

    Single<DataResp<LoginInfo>> autoLogin(String token);

    Single<DataResp<UserInfo>> getUserInfo(String token);
}
