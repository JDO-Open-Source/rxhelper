package com.jidouauto.eddie.mvpdemo.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jidouauto.eddie.mvpdemo.BaseActivity;
import com.jidouauto.eddie.mvpdemo.R;
import com.jidouauto.eddie.mvpdemo.bean.LoginInfo;
import com.jidouauto.eddie.mvpdemo.data.user.UserDataSourceImpl;


/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements UserContract.ILoginView {
    private AutoCompleteTextView etUserName;
    private EditText etPasswd;
    private View mProgressView;

    private UserContract.ILoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mPresenter = new LoginPresenter(this, new UserDataSourceImpl());
        mPresenter.subscribe();
    }

    private void initView() {
        etUserName = findViewById(R.id.tvUserName);
        etUserName.setText("zhangsan");
        etPasswd = findViewById(R.id.password);
        etPasswd.setText("123");
        etPasswd.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                checkToLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> checkToLogin());

        mProgressView = findViewById(R.id.login_progress);
    }

    private void checkToLogin() {
        String username = etUserName.getText().toString();
        String passwd = etPasswd.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            mPresenter.login(username, passwd);
        }
    }

    @Override
    public void startLogin() {
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void endLogin() {
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void loginSucceed(LoginInfo info) {
        Toast.makeText(this, "Welcome !" + info.getUsername(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, UserActivity.class));
    }

    @Override
    public void loginError(Throwable e) {
        errorHandler.handError(e,getString(R.string.login_failed));
    }

    @Override
    public void setPresenter(UserContract.ILoginPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}

