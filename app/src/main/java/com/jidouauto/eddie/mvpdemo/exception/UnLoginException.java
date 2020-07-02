package com.jidouauto.eddie.mvpdemo.exception;

public class UnLoginException extends BaseException {
    public UnLoginException(int code) {
        super(code);
    }

    public UnLoginException(int code, String message) {
        super(code, message);
    }

    public UnLoginException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public UnLoginException(int code, Throwable cause) {
        super(code, cause);
    }
}
