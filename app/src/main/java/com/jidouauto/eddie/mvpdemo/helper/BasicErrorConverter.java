package com.jidouauto.eddie.mvpdemo.helper;

import com.jidouauto.eddie.mvpdemo.exception.BaseException;
import com.jidouauto.eddie.mvpdemo.exception.DataException;
import com.jidouauto.eddie.mvpdemo.exception.NetworkException;
import com.jidouauto.eddie.mvpdemo.exception.UnknowException;
import com.jidouauto.lib.rxhelper.ErrorConverter;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class BasicErrorConverter implements ErrorConverter<BaseException> {

    public static final ErrorConverter INSTANCE = new BasicErrorConverter();

    /**
     * 将某个错误类型转换成特定的错误类型方便统一处理
     *
     * @param e 错误类型
     * @return base exception
     */
    @Override
    public BaseException convert(Throwable e) {
        if (e instanceof UnknownHostException
                || e instanceof ConnectException
                || e instanceof SocketTimeoutException
                || e instanceof IOException) {
            return new NetworkException(-1, "网络错误", e);
        } else if (e instanceof JSONException) {
            return new DataException(-1, "数据异常", e);
        } else if (e instanceof BaseException) {
            return (BaseException) e;
        } else {
            return new UnknowException(UnknowException.UNKNOW_CODE, e);
        }
    }
}
