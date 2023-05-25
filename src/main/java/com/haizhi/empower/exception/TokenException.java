package com.haizhi.empower.exception;

/**
 * 用户token类异常
 *
 * @author CristianWindy
 */
public class TokenException extends RuntimeException {

    public TokenException() {
        super("token异常");// 设置默认的提示信息
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(Throwable cause) {
        super("token异常", cause);
    }
}
