package com.jidouauto.lib.middleware.exception;

/**
 * The type Network exception.
 *
 * @author eddie 网络相关错误抛出此异常 eg. 服务超时 拒绝访问 连接错误 SOCKET异常 IO异常
 */
public class NetworkException extends BaseException {
    /**
     * Instantiates a new Network exception.
     *
     * @param code the code
     */
    public NetworkException(int code) {
        super(code);
    }

    /**
     * Instantiates a new Network exception.
     *
     * @param code    the code
     * @param message the message
     */
    public NetworkException(int code, String message) {
        super(code, message);
    }

    /**
     * Instantiates a new Network exception.
     *
     * @param code    the code
     * @param message the message
     * @param cause   the cause
     */
    public NetworkException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * Instantiates a new Network exception.
     *
     * @param code  the code
     * @param cause the cause
     */
    public NetworkException(int code, Throwable cause) {
        super(code, cause);
    }
}
