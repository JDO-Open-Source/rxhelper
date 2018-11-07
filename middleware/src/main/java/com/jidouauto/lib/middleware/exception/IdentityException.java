package com.jidouauto.lib.middleware.exception;

/**
 * The type Identity exception.
 *
 * @author eddie 身份校验异常时抛出此异常 eg. TOKEN错误 TOKEN失效 其它身份校验错误
 */
public class IdentityException extends BaseException {

    /**
     * Instantiates a new Identity exception.
     *
     * @param code the code
     */
    public IdentityException(int code) {
        super(code);
    }

    /**
     * Instantiates a new Identity exception.
     *
     * @param code    the code
     * @param message the message
     */
    public IdentityException(int code, String message) {
        super(code, message);
    }

    /**
     * Instantiates a new Identity exception.
     *
     * @param code    the code
     * @param message the message
     * @param cause   the cause
     */
    public IdentityException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * Instantiates a new Identity exception.
     *
     * @param code  the code
     * @param cause the cause
     */
    public IdentityException(int code, Throwable cause) {
        super(code, cause);
    }
}
