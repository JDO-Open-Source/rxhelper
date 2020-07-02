package com.jidouauto.eddie.mvpdemo.data;

import com.jidouauto.eddie.mvpdemo.MvpApplication;
import com.jidouauto.eddie.mvpdemo.api.ApiService;
import com.jidouauto.eddie.mvpdemo.api.FakeApiService;
import com.jidouauto.eddie.mvpdemo.config.HttpConfig;
import com.jidouauto.eddie.mvpdemo.config.HttpConfigImpl;
import com.jidouauto.eddie.mvpdemo.data.user.LocalUserDataSource;
import com.jidouauto.eddie.mvpdemo.data.user.LocalUserDataSourceImpl;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSourceImpl;

public class Repository {

    public static ApiService getApiService() {
        return new FakeApiService();
    }

    private static LocalUserDataSource localUserDataSource;

    public static synchronized LocalUserDataSource getLocalUserDataSource() {
        if(localUserDataSource == null) {
            localUserDataSource = new LocalUserDataSourceImpl();
        }

        return localUserDataSource;
    }

    public static HttpConfig getHttpConfig() {
        return new HttpConfigImpl(MvpApplication.getInstance());
    }

    public static UserDataSource getUserDataSource() {
        return new UserDataSourceImpl(getApiService(), getLocalUserDataSource());
    }

}
