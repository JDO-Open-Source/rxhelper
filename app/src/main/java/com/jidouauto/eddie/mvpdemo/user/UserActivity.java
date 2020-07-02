package com.jidouauto.eddie.mvpdemo.user;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.jidouauto.eddie.mvpdemo.BaseActivity;
import com.jidouauto.eddie.mvpdemo.LifecycleEvent;
import com.jidouauto.eddie.mvpdemo.R;
import com.jidouauto.eddie.mvpdemo.api.FakeApiService;
import com.jidouauto.eddie.mvpdemo.bean.UserInfo;
import com.jidouauto.eddie.mvpdemo.data.Repository;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSource;
import com.jidouauto.lib.rxhelper.transformer.LifecycleTransformer;

public class UserActivity extends BaseActivity implements UserContract.IUserView {
    UserContract.IUserPresenter userPresenter;
    UserDataSource mUserDataSource;
    SwitchCompat switchNetwork;
    Button btnGetUserInfo;
    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mUserDataSource = Repository.getUserDataSource();
        userPresenter = new UserPresenter(this, mUserDataSource);

        switchNetwork = findViewById(R.id.switchNetwork);

        switchNetwork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FakeApiService.setNetworkConnected(isChecked);
            }
        });

        FakeApiService
                .networkObservable
                .compose(LifecycleTransformer.bindUntilEvent(getLifecycleObservable(), LifecycleEvent.ON_DESTROY))
                .subscribe(b -> switchNetwork.setChecked(b));

        findViewById(R.id.btnChangeToken)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FakeApiService.invalidToken();
                    }
                });

        btnGetUserInfo = findViewById(R.id.btnGetUserInfo);

        progress = findViewById(R.id.progress);
    }

    public void getUerInfo(View view) {
        userPresenter.getUserInfo();
    }

    @Override
    public void startGetUserInfo() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void endGetUserInfo() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onGetUserInfoFailed(Throwable e) {
        btnGetUserInfo.setText(R.string.get_user_info);
        getErrorHandler().handError(e, getString(R.string.get_user_info_failed));
    }

    @Override
    public void onUserInfo(UserInfo userInfo) {
        btnGetUserInfo.setText(R.string.get_user_info);
        Toast.makeText(this, "成功获取到用户信息:\n" + userInfo.getUsername() + "\n" + userInfo.getAge(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(UserContract.IUserPresenter presenter) {
        userPresenter = presenter;
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void showRetryStatus(String status) {
        btnGetUserInfo.setText(status);
    }
}
