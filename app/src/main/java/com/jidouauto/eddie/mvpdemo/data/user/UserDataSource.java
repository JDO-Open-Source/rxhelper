package com.jidouauto.eddie.mvpdemo.data.user;

import com.jidouauto.eddie.mvpdemo.bean.DataResp;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.NullableDataResp;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

import io.reactivex.Single;
import io.reactivex.SingleSource;

public interface UserDataSource {
    Single<DataResp<LoginInfo>> login(String username, String password);

    Single<DataResp<String>> getToken();

    void expireToken();

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    Single<DataResp<UserInfo>> getUserInfo(String token);

    /**
     * 获取用户头像链接地址
     * 获取到的头像链接可能为空
     *
     * @param token
     * @return
     */
    Single<NullableDataResp<String>> getUserAvatar(String token);

    String getLocalToken();
}