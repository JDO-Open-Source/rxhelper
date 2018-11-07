package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.lib.middleware.DataSource;
import com.jidouauto.lib.middleware.exception.BaseException;
import com.jidouauto.lib.middleware.exception.DataException;

public class ResultResp<T> extends MsgResp implements DataSource<T> {


    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public void validateResult() throws BaseException {
        super.validateResult();
        if (data == null) {
            throw new DataException(getCode(), "Data is NULL!");
        }
    }

    @Override
    public String toString() {
        return "ResultResp{" +
                "code=" + getCode() +
                ",msg=" + getMessage() +
                ",data=" + data +
                '}';
    }
}
