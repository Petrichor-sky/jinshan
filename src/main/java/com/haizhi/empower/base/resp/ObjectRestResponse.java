package com.haizhi.empower.base.resp;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/3/23
 * @Copyright (c) 2009-2022 All rights reserved.
 */
public class ObjectRestResponse<T> extends BaseResponse {

    T data;



    public ObjectRestResponse<T> data(T data) {
        this.setData(data);
        return this;
    }

    public ObjectRestResponse(int status, String message) {
        super(status, message);
    }

    public ObjectRestResponse() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
