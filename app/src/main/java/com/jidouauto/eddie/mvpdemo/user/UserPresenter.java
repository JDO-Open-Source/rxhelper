package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.eddie.mvpdemo.exception.IdentityException;
import com.jidouauto.eddie.mvpdemo.helper.BasicErrorConverter;
import com.jidouauto.lib.rxhelper.backoff.FixedBackOffStrategy;
import com.jidouauto.lib.rxhelper.transformer.ErrorTransformers;
import com.jidouauto.lib.rxhelper.transformer.RetryListener;
import com.jidouauto.lib.rxhelper.transformer.RetryTransformers;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserPresenter extends BasePresenter implements UserContract.IUserPresenter {
    private UserContract.IUserView mUserView;
    private UserDataSource mUserDataSource;

    public UserPresenter(UserContract.IUserView userView, UserDataSource userDataSource) {
        super(userView);
        mUserView = userView;
        userView.setPresenter(this);
        mUserDataSource = userDataSource;
    }

    @Override
    public void getUserInfo() {
        mUserDataSource.getUserInfo()
                .compose(ErrorTransformers.convertError(BasicErrorConverter.INSTANCE))
                .compose(RetryTransformers.retryOnError(1, new FixedBackOffStrategy(1000), null, mUserDataSource.autoLogin().toObservable(), new RetryListener() {
                    @Override
                    public void scheduleRetry(Throwable retryError, int retryCount, long delay) {
                        mUserView.showRetryStatus("Token失效，计划" + delay + "ms后进行第" + retryCount + "次重试");
                    }

                    @Override
                    public void startRetry(Throwable retryError, int retryCount) {
                        mUserView.showRetryStatus("Token失效，" + "开始第" + retryCount + "次重试");
                    }
                }, IdentityException.class))
                .compose(RetryTransformers.retryExceptError(5, new FixedBackOffStrategy(1000), null, null, new RetryListener() {
                    @Override
                    public void scheduleRetry(Throwable retryError, int retryCount, long delay) {
                        mUserView.showRetryStatus(retryError.getMessage() + "，计划" + delay + "ms后进行第" + retryCount + "次重试");
                    }

                    @Override
                    public void startRetry(Throwable retryError, int retryCount) {
                        mUserView.showRetryStatus(retryError.getMessage() + "开始第" + retryCount + "次重试");
                    }
                }, IdentityException.class))
                .compose(bindUntilEvent(LifecycleEvent.ON_DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUserView.startGetUserInfo();
                    }

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        mUserView.endGetUserInfo();
                        mUserView.onUserInfo(userInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mUserView.endGetUserInfo();
                        mUserView.onGetUserInfoFailed(e);
                    }
                });
    }
}
