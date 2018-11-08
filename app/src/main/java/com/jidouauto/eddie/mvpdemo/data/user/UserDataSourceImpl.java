package com.jidouauto.eddie.mvpdemo.data.user;

import android.util.Log;

import com.jidouauto.eddie.mvpdemo.bean.DataResp;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.NullableDataResp;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class UserDataSourceImpl implements UserDataSource {
    private static final String TAG = "UserDataSourceImpl";

    public Observable<DataResp<LoginInfo>> login(String username, final String password) {
        Log.d(TAG, "login : " + username);
        //配合Retrofit+Rxjava2 返回数据
        ///模拟服务端返回数据
        return Observable.create(emitter -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataResp<LoginInfo> dataDataResp = new DataResp();
                    if ("zhangsan".equals(username)) {
                        if ("123".equals(password)) {
                            LoginInfo loginInfo = new LoginInfo();
                            loginInfo.setUsername(username);
                            loginInfo.setToken("tokenfortest");
                            dataDataResp.setData(loginInfo);
                            dataDataResp.setCode(1);
                        } else {
                            dataDataResp.setCode(0);
                            dataDataResp.setMessage("密码不正确!");
                        }
                    } else if ("datanull".equals(username)) {
                        dataDataResp.setCode(1);
                        dataDataResp.setData(null);
                    } else if ("messagenull".equals(username)) {
                        dataDataResp.setCode(0);
                        dataDataResp.setMessage(null);
                    } else if ("token".equals(username)) {
                        dataDataResp.setCode(999);
                        dataDataResp.setMessage("Token 失效");
                    } else {
                        dataDataResp.setCode(0);
                        dataDataResp.setMessage("用户不存在!");
                    }
                    emitter.onNext(dataDataResp);
                    emitter.onComplete();
                }
            }).start();
        });
    }

    @Override
    public Observable<DataResp<String>> getToken() {
        //配合Retrofit+Rxjava2 返回数据
        ///模拟服务端返回数据

        return Observable.create(emitter -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DataResp<String> tokenData = new DataResp<>();
                    tokenData.setCode(1);
                    Log.d(TAG, "fetch token : " + serverToken);
                    localToken = serverToken;
                    tokenData.setData(localToken);
                    tokenData.setCode(1);
                    emitter.onNext(tokenData);
                    emitter.onComplete();
                }
            }).start();
        });
    }

    String serverToken = UUID.randomUUID().toString();
    String localToken = serverToken;

    @Override
    public String getLocalToken() {
        return localToken;
    }

    @Override
    public void expireToken() {
        serverToken = UUID.randomUUID().toString();
    }

    @Override
    public Observable<DataResp<UserInfo>> getUserInfo(String token) {
        return Observable.create(emitter -> {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DataResp<UserInfo> resultDataResp = new DataResp<>();
                if (!serverToken.equals(token)) {
                    resultDataResp.setCode(999);
                    resultDataResp.setMessage("Token expire!");
                } else {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsername("zhangsan");
                    userInfo.setAge(18);
                    resultDataResp.setCode(1);
                    resultDataResp.setData(userInfo);
                }
                emitter.onNext(resultDataResp);
                emitter.onComplete();
            }).start();
        });
    }

    @Override
    public Observable<NullableDataResp<String>> getUserAvatar(String token) {
        return Observable.create(new ObservableOnSubscribe<NullableDataResp<String>>() {
            @Override
            public void subscribe(ObservableEmitter<NullableDataResp<String>> emitter) throws Exception {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String avatar = null;

                        NullableDataResp<String> resp = new NullableDataResp<>();
                        resp.setData(avatar);
                        emitter.onNext(resp);
                        emitter.onComplete();
                    }
                }).start();
            }
        });
    }
}