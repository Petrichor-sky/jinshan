package com.haizhi.empower.exception;

/**
 * 消息推送相关异常
 *
 * @author chenb
 * @date 2022年12月07日11:17:31
 */
public class MsgException extends RuntimeException {

    public MsgException() {
        super("消息服务异常");// 设置默认的提示信息
    }

    public MsgException(String message) {
        super(message);
    }

    public MsgException(String message, Throwable cause) {
        super(message, cause);
    }

    public MsgException(Throwable cause) {
        super("消息服务异常", cause);
    }
}
