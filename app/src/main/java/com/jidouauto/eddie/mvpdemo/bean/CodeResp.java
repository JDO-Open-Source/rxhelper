package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.lib.middleware.exception.IdentityException;
import com.jidouauto.lib.middleware.IdentityValidator;

public class CodeResp implements IdentityValidator {
    public static final int SUCCEED = 1;
    public static final int TOKEN_EXPIRE = 999;

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public void validateIdentity() throws IdentityException {
        if (getCode() == TOKEN_EXPIRE) {
            throw new IdentityException(getCode(), "token expire!");
        }
    }

    @Override
    public String toString() {
        return "CodeResp{" +
                "code=" + code +
                '}';
    }
}
