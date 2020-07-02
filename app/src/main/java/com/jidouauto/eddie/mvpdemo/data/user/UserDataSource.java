package com.jidouauto.eddie.mvpdemo.data.user;

import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

import io.reactivex.Single;

public interface UserDataSource {
    /**
     * 用户主动登录
     *
     * @param username
     * @param password
     * @return
     */
    Single<LoginInfo> login(String username, String password);

    /**
     * 自动登录，token续期
     *
     * @return
     */
    Single<LoginInfo> autoLogin();

    /**
     * 获取登录的信息，如果未登录则回调Error{@link com.jidouauto.eddie.mvpdemo.exception.UnLoginException}
     *
     * @return
     */
    Single<LoginInfo> getLoginInfo();

    /**
     * 请求用户的详细信息
     *
     * @return
     */
    Single<UserInfo> getUserInfo();
}