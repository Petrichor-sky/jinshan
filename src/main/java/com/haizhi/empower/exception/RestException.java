package com.haizhi.empower.exception;

/**
 * rest请求基础错误类型
 *
 * @author CristianWindy
 */
public class RestException extends RuntimeException {

    public RestException() {
        super("rest请求出错");// 设置默认的提示信息
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(Throwable cause) {
        super("rest请求出错", cause);
    }
}
