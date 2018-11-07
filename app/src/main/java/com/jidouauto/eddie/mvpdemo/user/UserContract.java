package com.jidouauto.eddie.mvpdemo.user;

import com.jidouauto.eddie.mvpdemo.IBasePresenter;
import com.jidouauto.eddie.mvpdemo.IBaseView;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;

public class UserContract {
    public interface ILoginView extends IBaseView<UserContract.ILoginPresenter> {
        void startLogin();                          //开始登陆

        void endLogin();                          //登陆结束

        void loginSucceed(LoginInfo info);          //登陆成功

        void loginError(Throwable e);              //发生错误
    }

    public interface ILoginPresenter extends IBasePresenter {
        void login(String username, String password);         //用户登陆业务
    }

    public interface IUserView extends IBaseView<UserContract.IUserPresenter> {
        void startGetUserInfo();

        void endGetUserInfo();

        void onGetUserInfoFailed(Throwable e);

        void onUserInfo(UserInfo userInfo);
    }

    public interface IUserPresenter extends IBasePresenter {
        void getUserInfo();
    }
}
