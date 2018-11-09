package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.lib.rxhelper.DataConverter;
import com.jidouauto.lib.rxhelper.NullableData;

public class NullableDataResp<T> extends MsgResp implements DataConverter<NullableData<T>> {

    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public NullableData<T> convert() {
        return NullableData.of(getData());
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
