package com.jidouauto.lib.middleware;

/**
 * @author eddie
 * <p>
 * 自定义错误类型转换接口
 */
public interface ErrorConverter<T extends Throwable> {
    /**
     * Convert base exception.
     *
     * @param e the e
     * @return the base exception
     */
    T convert(Throwable e);
}