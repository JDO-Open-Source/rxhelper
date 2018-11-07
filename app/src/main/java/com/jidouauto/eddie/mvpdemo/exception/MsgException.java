package com.jidouauto.eddie.mvpdemo.exception;

/**
 * created by di at 2018/10/15
 */
public class MsgException extends CodeException {
    public MsgException(int code) {
        super(code);
    }

    public MsgException(int code, String message) {
        super(code, message);
    }

    public MsgException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public MsgException(int code, Throwable cause) {
        super(code, cause);
    }
}
