package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.lib.middleware.exception.IdentityException;
import com.jidouauto.lib.middleware.transformer.StreamTransformer;

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
                .compose(StreamTransformer.validate())             //校验后端返回数据正确性
                .compose(StreamTransformer.convertToData())        //数据转换
                .compose(StreamTransformer.validate())             //校验转换后的数据的合法性
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

    @Override
    public void getUserAvatar() {
        Observable
                .fromCallable(() -> mUserDataSource.getLocalToken())
                .flatMap(token -> mUserDataSource.getUserAvatar(token))
                .compose(StreamTransformer.validate())
                .compose(StreamTransformer.convertToData("https://xxxxxx.xxx.xxx/user/default.png"))    //如果服务端的头像地址数据为空，则使用默认的头像
                .compose(StreamTransformer.retryWhenError(IdentityException.class, 1, 0, mUserDataSource.getToken()))
                .compose(StreamTransformer.convertError())
                .compose(bindUntilEvent(LifecycleEvent.ON_DESTROY))
                .compose(StreamTransformer.applyIOUI())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUserView.startGetUserAvatar();
                    }

                    @Override
                    public void onNext(String avatar) {
                        mUserView.endGetUserAvatar();
                        mUserView.onUserAvatar(avatar);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.getUserAvatarError(e);
                        mUserView.endGetUserAvatar();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
