package com.jidouauto.eddie.mvpdemo.data.user;

import com.jidouauto.eddie.mvpdemo.api.ApiService;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.exception.UnLoginException;
import com.jidouauto.lib.rxhelper.transformer.DataTransformers;
import com.jidouauto.lib.rxhelper.transformer.ValidateTransformers;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class UserDataSourceImpl implements UserDataSource {
    private static final String TAG = "UserDataSourceImpl";

    private ApiService apiService;
    private LocalUserDataSource localUserDataSource;

    public UserDataSourceImpl(ApiService apiService, LocalUserDataSource localUserDataSource) {
        this.apiService = apiService;
        this.localUserDataSource = localUserDataSource;
    }

    @Override
    public Single<LoginInfo> login(String username, final String password) {
        return apiService
                .login(username, password)
                .compose(ValidateTransformers.validate())
                .compose(DataTransformers.convertToData())
                .compose(ValidateTransformers.validate())
                .subscribeOn(Schedulers.io())
                .doOnSuccess(loginInfo -> localUserDataSource.saveLoginInfo(loginInfo));
    }

    @Override
    public Single<LoginInfo> autoLogin() {
        return getLoginInfo()
                .flatMap(loginInfo -> apiService.autoLogin(loginInfo.getToken()).subscribeOn(Schedulers.io()))
                .compose(ValidateTransformers.validate())
                .compose(DataTransformers.convertToData())
                .compose(ValidateTransformers.validate())
                .subscribeOn(Schedulers.io())
                .doOnSuccess(loginInfo -> localUserDataSource.saveLoginInfo(loginInfo));
    }

    @Override
    public Single<LoginInfo> getLoginInfo() {
        return Single.fromCallable(new Callable<LoginInfo>() {
            @Override
            public LoginInfo call() throws Exception {
                LoginInfo loginInfo = localUserDataSource.getSavedLoginInfo();
                if (loginInfo == null) {
                    throw new UnLoginException(-1, "user not login!");
                } else {
                    return loginInfo;
                }
            }
        });
    }

    @Override
    public Single<UserInfo> getUserInfo() {
        //mock network request
        return getLoginInfo()
                .flatMap(loginInfo -> apiService.getUserInfo(loginInfo.getToken()))
                //数据校验
                .compose(ValidateTransformers.validate())
                //数据转换
                .compose(DataTransformers.convertToData())
                //数据校验
                .compose(ValidateTransformers.validate())
                .subscribeOn(Schedulers.io());

    }

}