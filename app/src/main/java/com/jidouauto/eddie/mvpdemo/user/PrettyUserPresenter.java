package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.api.FakeApiService;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.eddie.mvpdemo.exception.IdentityException;
import com.jidouauto.eddie.mvpdemo.helper.BasicErrorConverter;
import com.jidouauto.lib.rxhelper.backoff.FixedBackOffStrategy;
import com.jidouauto.lib.rxhelper.transformer.DataTransformers;
import com.jidouauto.lib.rxhelper.transformer.ErrorTransformers;
import com.jidouauto.lib.rxhelper.transformer.LifecycleTransformer;
import com.jidouauto.lib.rxhelper.transformer.RetryListener;
import com.jidouauto.lib.rxhelper.transformer.RetryTransformers;
import com.jidouauto.lib.rxhelper.transformer.ValidateTransformers;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PrettyUserPresenter extends BasePresenter implements UserContract.IUserPresenter {
    private UserContract.IUserView mUserView;
    private UserDataSource mUserDataSource;

    public PrettyUserPresenter(UserContract.IUserView userView, UserDataSource userDataSource) {
        super(userView);
        mUserView = userView;
        userView.setPresenter(this);
        mUserDataSource = userDataSource;
    }

    private void requestToken(String token) {
        new FakeApiService()
                .getUserInfo(token)
                //数据校验
                .compose(ValidateTransformers.validate())
                //数据转换
                .compose(DataTransformers.convertToData())
                //数据校验
                .compose(ValidateTransformers.validate())
                //错误转换
                .compose(ErrorTransformers.convertError(BasicErrorConverter.INSTANCE))
                //自动登录重试
                .compose(RetryTransformers.retryOnError(1, 1000, mUserDataSource.autoLogin().toObservable(), IdentityException.class))
                //其它错误重试
                .compose(RetryTransformers.retryExceptError(5, 1000, IdentityException.class))
                //绑定生命周期
                .compose(LifecycleTransformer.bindUntilEvent(mUserView.getLifecycleObservable(), LifecycleEvent.ON_DESTROY))
                .subscribeOn(Schedulers.io())
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

    @Override
    public void getUserInfo() {
        mUserDataSource.getUserInfo()
                //错误转换
                .compose(ErrorTransformers.convertError(BasicErrorConverter.INSTANCE))
                //自动登录重试
                .compose(RetryTransformers.retryOnError(1, 1000, mUserDataSource.autoLogin().toObservable(), IdentityException.class))
                //其它错误重试
                .compose(RetryTransformers.retryExceptError(5, 1000, IdentityException.class))
                .compose(LifecycleTransformer.bindUntilEvent(mUserView.getLifecycleObservable(), LifecycleEvent.ON_DESTROY))
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
