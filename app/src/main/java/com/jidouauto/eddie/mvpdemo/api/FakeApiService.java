package com.jidouauto.eddie.mvpdemo.api;

import com.jidouauto.eddie.mvpdemo.bean.DataResp;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.exception.IdentityException;

import java.net.ConnectException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

public class FakeApiService implements ApiService {

    public static String CURRENT_TOKEN = UUID.randomUUID().toString();

    private static boolean NETWORK_CONNECTED = true;

    public static BehaviorSubject<String> tokenObservable = BehaviorSubject.createDefault(CURRENT_TOKEN);
    public static BehaviorSubject<Boolean> networkObservable = BehaviorSubject.createDefault(NETWORK_CONNECTED);

    /**
     * make the token expired by simulate a new token
     */
    public static void invalidToken() {
        CURRENT_TOKEN = UUID.randomUUID().toString();
        tokenObservable.onNext(CURRENT_TOKEN);
    }

    /**
     * simulate network connect status
     *
     * @param networkConnected
     */
    public static void setNetworkConnected(boolean networkConnected) {
        if (NETWORK_CONNECTED ^ networkConnected) {
            NETWORK_CONNECTED = networkConnected;
            networkObservable.onNext(networkConnected);
        }
    }

    @Override
    public Single<DataResp<LoginInfo>> login(String userName, String password) {
        return Single
                .fromCallable(() -> {
                    if (!NETWORK_CONNECTED) {
                        Thread.sleep(1000);
                        throw new ConnectException("network disconnected");
                    }

                    DataResp<LoginInfo> loginInfoDataResp = new DataResp<>();
                    if ("zhangsan".equals(userName) && "123".equals(password)) {
                        LoginInfo loginInfo = new LoginInfo();
                        loginInfo.setToken(CURRENT_TOKEN);
                        loginInfo.setUsername("testUser");

                        loginInfoDataResp.setCode(0);
                        loginInfoDataResp.setData(loginInfo);
                    } else {
                        loginInfoDataResp.setCode(1);
                        loginInfoDataResp.setMessage("用户名或密码不正确");
                    }

                    return loginInfoDataResp;

                })
                .delay(1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Single<DataResp<LoginInfo>> autoLogin(String token) {
        return Single
                .fromCallable(new Callable<DataResp<LoginInfo>>() {
                    @Override
                    public DataResp<LoginInfo> call() throws Exception {
                        if (!NETWORK_CONNECTED) {
                            Thread.sleep(1000);
                            throw new ConnectException("network disconnected");
                        }

                        LoginInfo loginInfo = new LoginInfo();
                        loginInfo.setToken(CURRENT_TOKEN);
                        loginInfo.setUsername("testUser2");

                        DataResp<LoginInfo> loginInfoDataResp = new DataResp<>();
                        loginInfoDataResp.setCode(0);
                        loginInfoDataResp.setData(loginInfo);

                        return loginInfoDataResp;
                    }
                })
                .delay(1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Single<DataResp<UserInfo>> getUserInfo(String token) {
        return Single
                .fromCallable(new Callable<DataResp<UserInfo>>() {
                    @Override
                    public DataResp<UserInfo> call() throws Exception {
                        if (!NETWORK_CONNECTED) {
                            Thread.sleep(1000);
                            throw new ConnectException("network disconnected");
                        }

                        if (!CURRENT_TOKEN.equals(token)) {
                            Thread.sleep(1000);
                            throw new IdentityException(-1, "token expired!");
                        }

                        UserInfo userInfo = new UserInfo();
                        userInfo.setAge(22);
                        userInfo.setUsername("testUser");

                        DataResp<UserInfo> userInfoDataResp = new DataResp<>();
                        userInfoDataResp.setCode(0);
                        userInfoDataResp.setData(userInfo);

                        return userInfoDataResp;
                    }
                })
                .delay(1000, TimeUnit.MILLISECONDS);


    }
}
