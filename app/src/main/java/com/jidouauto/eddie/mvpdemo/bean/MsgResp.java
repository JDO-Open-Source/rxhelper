package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.eddie.mvpdemo.exception.MsgException;
import com.jidouauto.lib.base.utils.StringUtils;
import com.jidouauto.lib.middleware.Validator;
import com.jidouauto.lib.middleware.exception.BaseException;
import com.jidouauto.lib.middleware.exception.DataException;

public class MsgResp extends CodeResp implements Validator {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void validate() throws BaseException {
        //服务器返回的错误状态，但是没有说明错误原因
        if (getCode() != SUCCEED) {
            if (StringUtils.isTrimEmpty(getMessage())) {
                throw new DataException(getCode(), "Error message is empty!");
            } else {
                throw new MsgException(getCode(), getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "MsgResp{" +
                "message='" + message + '\'' +
                '}';
    }
}
