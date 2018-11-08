package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.exception.BaseException;

/**
 * @author eddie
 * <p>
 * 自定义错误类型转换接口
 */
public interface ErrorConverter {
    /**
     * Convert base exception.
     *
     * @param e the e
     * @return the base exception
     */
    BaseException convert(Throwable e);
}