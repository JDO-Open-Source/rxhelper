package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.BasePresenter;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.eddie.mvpdemo.helper.BasicErrorConverter;
import com.jidouauto.eddie.mvpdemo.exception.IdentityException;
import com.jidouauto.lib.rxhelper.transformer.Transformers;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
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
        Single
                .fromCallable(() -> mUserDataSource.getLocalToken())
                .flatMap(token -> mUserDataSource.getUserInfo(token))
                .compose(Transformers.validate())             //校验后端返回数据正确性
                .compose(Transformers.convertToData())        //数据转换
                .compose(Transformers.validate())             //校验转换后的数据的合法性
                .compose(Transformers.retryWhenError(IdentityException.class, 1, 0, mUserDataSource.getToken()))
                .compose(Transformers.convertError(BasicErrorConverter.INSTANCE))
                .compose(bindUntilEvent(LifecycleEvent.ON_DESTROY))
                .compose(Transformers.applyIOUI())
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
    public void getUserAvatar() {
        Single
                .fromCallable(() -> mUserDataSource.getLocalToken())
                .flatMap(token -> mUserDataSource.getUserAvatar(token))
                .compose(Transformers.validate())
                .compose(Transformers.convertToData("https://xxxxxx.xxx.xxx/user/default.png"))    //如果服务端的头像地址数据为空，则使用默认的头像
                .compose(Transformers.retryWhenError(IdentityException.class, 1, 0, mUserDataSource.getToken()))
                .compose(Transformers.convertError(BasicErrorConverter.INSTANCE))
                .compose(bindUntilEvent(LifecycleEvent.ON_DESTROY))
                .compose(Transformers.applyIOUI())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUserView.startGetUserAvatar();
                    }

                    @Override
                    public void onSuccess(String avatar) {
                        mUserView.endGetUserAvatar();
                        mUserView.onUserAvatar(avatar);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mUserView.endGetUserAvatar();
                        mUserView.getUserAvatarError(e);
                    }
                });

    }
}
