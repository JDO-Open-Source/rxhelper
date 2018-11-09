package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.eddie.mvpdemo.exception.BaseException;
import com.jidouauto.eddie.mvpdemo.exception.DataException;
import com.jidouauto.lib.middleware.Validator;

public class LoginInfo implements Validator {
    private String username;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void validate() throws BaseException {
        if (username == null || token == null) {
            throw new DataException(-1, "Data error!");
        }
    }
}
