package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.lib.middleware.transformer.StreamTransformer;
import com.jidouauto.lib.middleware.exception.IdentityException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class UserPresenter extends BasePresenter implements UserContract.IUserPresenter {
    private UserContract.IUserView mUserView;
    private UserDataSource mUserDataSource;

    public UserPresenter(UserContract.IUserView userView, UserDataSource userDataSource) {
        super(userView);
        mUserView = userView;
        userView.setPresenter(this);
        mUserDataSource = userDataSource;
        StreamTransformer.debug = true;
    }

    @Override
    public void getUserInfo() {
        Observable
                .fromCallable(() -> mUserDataSource.getLocalToken())
                .flatMap(token -> mUserDataSource.getUserInfo(token))
                .compose(StreamTransformer.validateIdentity())
                .compose(StreamTransformer.validateResult())
                .compose(StreamTransformer.convertToData())
                .compose(StreamTransformer.retryWhenError(IdentityException.class, 1, 0, mUserDataSource.getToken()))
                .compose(StreamTransformer.convertError())
                .compose(bindUntilEvent(LifecycleEvent.ON_DESTROY))
                .compose(StreamTransformer.applyIOUI())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUserView.startGetUserInfo();
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        mUserView.endGetUserInfo();
                        mUserView.onUserInfo(userInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.endGetUserInfo();
                        mUserView.onGetUserInfoFailed(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
