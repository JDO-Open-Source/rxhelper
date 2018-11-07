package com.jidouauto.lib.middleware.exception;

/**
 * The type Base exception.
 *
 * @author eddie 只作为基类使用，不允许直接使用
 */
public class BaseException extends Exception {

    private int code;

    /**
     * Instantiates a new Base exception.
     *
     * @param code the code
     */
    protected BaseException(int code) {
        super();
        this.code = code;
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param code    the code
     * @param message the message
     */
    protected BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param code    the code
     * @param message the message
     * @param cause   the cause
     */
    protected BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param code  the code
     * @param cause the cause
     */
    protected BaseException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
