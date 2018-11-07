package com.jidouauto.lib.middleware.exception;

/**
 * The type Unknow exception.
 *
 * @author eddie 未知错误抛出此异常 eg. 未考虑到的异常
 */
public class UnknowException extends BaseException {
    /**
     * The constant UNKNOW_CODE.
     */
    public static final int UNKNOW_CODE = -1;

    /**
     * Instantiates a new Unknow exception.
     *
     * @param code the code
     */
    public UnknowException(int code) {
        super(code);
    }

    /**
     * Instantiates a new Unknow exception.
     *
     * @param code    the code
     * @param message the message
     */
    public UnknowException(int code, String message) {
        super(code, message);
    }

    /**
     * Instantiates a new Unknow exception.
     *
     * @param code    the code
     * @param message the message
     * @param cause   the cause
     */
    public UnknowException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * Instantiates a new Unknow exception.
     *
     * @param code  the code
     * @param cause the cause
     */
    public UnknowException(int code, Throwable cause) {
        super(code, cause);
    }
}
