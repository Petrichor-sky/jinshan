package com.haizhi.empower.config;

import com.haizhi.empower.base.AppConstants;
import com.haizhi.empower.base.ThreadLocalContext;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.exception.ParamException;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.exception.ServiceException;
import com.haizhi.empower.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截
 *
 * @author CristianWindy
 * @Date: 2020/7/7 01:16
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * token失效异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(TokenException.class)
    public BaseResponse TokenExceptionHandler(TokenException ex) {
        log.error("请求【" + ThreadLocalContext.get(AppConstants.Key.URI) + "】携带的token为空或已失效", ex);
        return new BaseResponse(AppConstants.DefaultResponse.TOKEN_ERROR, ex.getMessage());
    }

    /**
     * 请求参数异常
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ParamException.class)
    public BaseResponse paramExceptionHandler(ParamException ex) {
        log.error("请求【" + ThreadLocalContext.get(AppConstants.Key.URI) + "】出错，错误信息为：", ex);
        return new BaseResponse(AppConstants.DefaultResponse.ERROR, ex.getMessage());
    }

    /**
     * 服务层异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    public BaseResponse ServiceExceptionHandler(ServiceException ex) {
        log.error("请求【" + ThreadLocalContext.get(AppConstants.Key.URI) + "】出错，错误信息为：", ex);
        return new BaseResponse(AppConstants.DefaultResponse.ERROR, ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse notValidBean(MethodArgumentNotValidException e) {
        return new BaseResponse(AppConstants.DefaultResponse.ERROR, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 统一处理rest请求异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(RestException.class)
    public BaseResponse RestExceptionHandler(RestException ex) {
        log.error("请求【" + ThreadLocalContext.get(AppConstants.Key.URI) + "】出错，错误信息为：", ex);
        return new BaseResponse(AppConstants.DefaultResponse.ERROR, ex.getMessage());
    }

    /**
     * 其他异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse otherExceptionHandler(Exception ex) {
        log.error("请求【" + ThreadLocalContext.get(AppConstants.Key.URI) + "】抛出异常，错误信息为：", ex);
        return new BaseResponse(AppConstants.DefaultResponse.ERROR, ex.getMessage());
    }
}
