package com.jidouauto.eddie.mvpdemo.exception;


public class CodeException extends BaseException {
    public CodeException(int code) {
        super(code);
    }

    public CodeException(int code, String message) {
        super(code, message);
    }

    public CodeException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public CodeException(int code, Throwable cause) {
        super(code, cause);
    }
}
