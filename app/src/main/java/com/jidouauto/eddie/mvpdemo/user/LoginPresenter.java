package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.lib.middleware.transformer.StreamTransformer;

public class LoginPresenter extends BasePresenter implements UserContract.ILoginPresenter {
    private UserContract.ILoginView mLoginView;
    private UserDataSource mUserDataSource;

    public LoginPresenter(UserContract.ILoginView loginView, UserDataSource userDataSource) {
        super(loginView);
        mLoginView = loginView;
        mLoginView.setPresenter(this);
        mUserDataSource = userDataSource;
    }

    @Override
    public void login(String username, String password) {
        mUserDataSource.login(username, password)
                .compose(StreamTransformer.validate())              //校验后端返回数据正确性
                .compose(StreamTransformer.convertToData())         //数据转换
                .compose(StreamTransformer.validate())              //校验转换后的数据的合法性
                .compose(StreamTransformer.retryAnyError(1, 20)) //重试机制
                .compose(StreamTransformer.convertError())            //将错误类型转换为可知的错误类型便于前台处理
                .compose(StreamTransformer.applyIOUI())               //线程切换模式
                .compose(bindUntilEvent(LifecycleEvent.ON_DESTROY))   //ON_DESTROY事件的时候取消订阅事件
                .doOnSubscribe(disposable1 -> {
                    mLoginView.startLogin();
                })
                .subscribe(loginInfo -> {
                    mLoginView.endLogin();
                    mLoginView.loginSucceed(loginInfo);
                }, e -> {
                    e.printStackTrace();
                    mLoginView.endLogin();
                    mLoginView.loginError(e);
                });
    }
}