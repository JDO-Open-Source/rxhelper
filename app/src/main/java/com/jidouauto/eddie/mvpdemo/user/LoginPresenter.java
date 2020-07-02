package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.eddie.mvpdemo.helper.BasicErrorConverter;
import com.jidouauto.lib.rxhelper.backoff.FixedBackOffStrategy;
import com.jidouauto.lib.rxhelper.transformer.ErrorTransformers;
import com.jidouauto.lib.rxhelper.transformer.RetryListener;
import com.jidouauto.lib.rxhelper.transformer.RetryTransformers;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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
                .compose(ErrorTransformers.convertError(BasicErrorConverter.INSTANCE))
                .compose(RetryTransformers.retryAnyError(5, new FixedBackOffStrategy(1000), null, null, new RetryListener() {
                    @Override
                    public void scheduleRetry(Throwable retryOnError, int retryCount, long delay) {
                        mLoginView.showRetryStatus("请求失败，计划" + delay + "ms后进行第" + retryCount + "次重试");
                    }

                    @Override
                    public void startRetry(Throwable retryOnError, int retryCount) {
                        mLoginView.showRetryStatus("开始第" + retryCount + "次重试");
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<LoginInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoginView.startLogin();
                    }

                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        mLoginView.endLogin();
                        mLoginView.loginSucceed(loginInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mLoginView.endLogin();
                        mLoginView.loginError(e);
                    }
                });
    }
}