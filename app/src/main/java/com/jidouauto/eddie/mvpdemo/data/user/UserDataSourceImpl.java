package com.jidouauto.eddie.mvpdemo.data.user;

import android.util.Log;

import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.ResultResp;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

import java.util.UUID;

import io.reactivex.Observable;

public class UserDataSourceImpl implements UserDataSource {
    private static final String TAG = "UserDataSourceImpl";

    public Observable<ResultResp<LoginInfo>> login(String username, final String password) {
        Log.d(TAG, "login : " + username);
        //配合Retrofit+Rxjava2 返回数据
        ///模拟服务端返回数据
        return Observable.create(emitter -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ResultResp<LoginInfo> dataResp = new ResultResp();
                    if ("zhangsan".equals(username)) {
                        if ("123".equals(password)) {
                            LoginInfo loginInfo = new LoginInfo();
                            loginInfo.setUsername(username);
                            loginInfo.setToken("tokenfortest");
                            dataResp.setData(loginInfo);
                            dataResp.setCode(1);
                        } else {
                            dataResp.setCode(0);
                            dataResp.setMessage("密码不正确!");
                        }
                    } else if ("datanull".equals(username)) {
                        dataResp.setCode(1);
                        dataResp.setData(null);
                    } else if ("messagenull".equals(username)) {
                        dataResp.setCode(0);
                        dataResp.setMessage(null);
                    } else if ("token".equals(username)) {
                        dataResp.setCode(999);
                        dataResp.setMessage("Token 失效");
                    } else {
                        dataResp.setCode(0);
                        dataResp.setMessage("用户不存在!");
                    }
                    emitter.onNext(dataResp);
                    emitter.onComplete();
                }
            }).start();
        });
    }

    @Override
    public Observable<ResultResp<String>> getToken() {
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
                    ResultResp<String> tokenData = new ResultResp<>();
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
    public Observable<ResultResp<UserInfo>> getUserInfo(String token) {
        return Observable.create(emitter -> {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ResultResp<UserInfo> resultResp = new ResultResp<>();
                if (!serverToken.equals(token)) {
                    resultResp.setCode(999);
                    resultResp.setMessage("Token expire!");
                } else {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsername("zhangsan");
                    userInfo.setAge(18);
                    resultResp.setCode(1);
                    resultResp.setData(userInfo);
                }
                emitter.onNext(resultResp);
                emitter.onComplete();
            }).start();
        });
    }
}