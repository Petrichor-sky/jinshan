package com.haizhi.empower.exception;

/**
 * @author CristianWindy
 * @ClassName: ServiceException
 * @date: 2018年12月28日 下午8:35:41
 * @Copyright: 2018 All rights reserved.
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
        super("服务层出错");// 设置默认的提示信息
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super("服务层出错", cause);
    }
}
