package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.lib.middleware.DataConverter;
import com.jidouauto.lib.middleware.exception.BaseException;
import com.jidouauto.lib.middleware.exception.DataException;

public class DataResp<T> extends MsgResp implements DataConverter<T> {


    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public T convert() {
        return getData();
    }

    @Override
    public void validate() throws BaseException {
        super.validate();
        if (data == null) {
            throw new DataException(getCode(), "Data is NULL!");
        }
    }

    @Override
    public String toString() {
        return "DataResp{" +
                "code=" + getCode() +
                ",msg=" + getMessage() +
                ",data=" + data +
                '}';
    }
}
