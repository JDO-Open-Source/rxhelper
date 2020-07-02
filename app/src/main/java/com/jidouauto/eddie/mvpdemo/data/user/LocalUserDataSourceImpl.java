package com.jidouauto.eddie.mvpdemo.data.user;

import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;

public class LocalUserDataSourceImpl implements LocalUserDataSource {

    LoginInfo cachedLoginInfo;

    public LocalUserDataSourceImpl() {

    }

    @Override
    public void saveLoginInfo(LoginInfo loginInfo) {
        cachedLoginInfo = loginInfo;
    }

    @Override
    public LoginInfo getSavedLoginInfo() {
        return cachedLoginInfo;
    }
}
