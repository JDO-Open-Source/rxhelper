package com.jidouauto.lib.middleware.exception;

/**
 * The type Data exception.
 *
 * @author eddie 数据异常时抛出此异常 eg. 应该有内容的情况内容为空 内容状态错误 数据逻辑错误 数据格式错误 数据解析错误 JSON 错误
 */
public class DataException extends BaseException {
    /**
     * Instantiates a new Data exception.
     *
     * @param code the code
     */
    public DataException(int code) {
        super(code);
    }

    /**
     * Instantiates a new Data exception.
     *
     * @param code    the code
     * @param message the message
     */
    public DataException(int code, String message) {
        super(code, message);
    }

    /**
     * Instantiates a new Data exception.
     *
     * @param code    the code
     * @param message the message
     * @param cause   the cause
     */
    public DataException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * Instantiates a new Data exception.
     *
     * @param code  the code
     * @param cause the cause
     */
    public DataException(int code, Throwable cause) {
        super(code, cause);
    }
}
