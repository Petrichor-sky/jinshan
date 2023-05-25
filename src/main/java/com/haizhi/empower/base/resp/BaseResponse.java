package com.haizhi.empower.base.resp;


import static com.haizhi.empower.base.AppConstants.DefaultResponse.*;
import static jdk.nashorn.tools.Shell.SUCCESS;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/3/23
 * @Copyright (c) 2009-2022 All rights reserved.
 */
public class BaseResponse {

    /**
     * 状态，默认200是成功状态
     */
    private int status = SUCCESS;
    /**
     * 状message
     */
    private String message = SUCCESS_MSG;

    private String traceId;

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BaseResponse(int status, String message, String traceId) {
        this.status = status;
        this.message = message;
        this.traceId = traceId;
    }

    public BaseResponse traceId(String traceId) {
        this.setTraceId(traceId);
        return this;
    }

    public BaseResponse(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
