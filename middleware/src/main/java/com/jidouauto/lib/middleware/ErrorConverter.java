package com.jidouauto.lib.middleware;

/**
 * @author eddie
 * <p>
 * 自定义错误类型转换接口
 */
public interface ErrorConverter<T extends Throwable> {
    /**
     * 对Throwable进行转换
     *
     * @param e 需要转换的异常
     * @return 转换后的异常
     */
    T convert(Throwable e);
}