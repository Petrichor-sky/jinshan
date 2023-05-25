package com.haizhi.empower.exception;

/**
 * 请求参数异常统一处理
 * Created by fantexi on 2019-06-27
 */
public class ParamException extends RuntimeException{

    public ParamException() {
        super("请求参数异常");// 设置默认的提示信息
    }

    public ParamException(String message) {
        super(message);
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamException(Throwable cause) {
        super("请求参数异常", cause);
    }
}
