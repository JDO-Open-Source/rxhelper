package com.jidouauto.eddie.mvpdemo.data.user;

import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;

public interface LocalUserDataSource {

    void saveLoginInfo(LoginInfo loginInfo);

    LoginInfo getSavedLoginInfo();

}
