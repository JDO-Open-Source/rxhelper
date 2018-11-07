package com.jidouauto.eddie.mvpdemo.user;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.jidouauto.eddie.mvpdemo.BaseActivity;
import com.jidouauto.eddie.mvpdemo.R;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSourceImpl;

public class UserActivity extends BaseActivity implements UserContract.IUserView {
    UserContract.IUserPresenter userPresenter;
    UserDataSource mUserDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mUserDataSource = new UserDataSourceImpl();
        userPresenter = new UserPresenter(this, mUserDataSource);
    }

    public void invalidateToken(View view) {
        mUserDataSource.expireToken();
    }

    public void getUerInfo(View view) {
        userPresenter.getUserInfo();
    }

    @Override
    public void startGetUserInfo() {
        getLoadingView().showLoading("GetUserInfo", getString(R.string.getting_user_info));
    }

    @Override
    public void endGetUserInfo() {
        getLoadingView().cancel("GetUserInfo");
    }

    @Override
    public void onGetUserInfoFailed(Throwable e) {
        getErrorHandler().handError(e, getString(R.string.get_user_info_failed));
    }

    @Override
    public void onUserInfo(UserInfo userInfo) {
        Toast.makeText(this, "登陆成功了:" + userInfo.getUsername() + "-" + userInfo.getAge(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(UserContract.IUserPresenter presenter) {
        userPresenter = presenter;
    }

    @Override
    public void finish(){
        super.finish();
        getLoadingView().cancelAll();
    }
}
