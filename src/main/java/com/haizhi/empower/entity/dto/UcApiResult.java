//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haizhi.empower.entity.dto;

import java.io.Serializable;

public class UcApiResult<T> implements Serializable {
    private static final long serialVersionUID = 5631370680343710179L;
    private String status;
    private String errstr;
    private T result;
    private String trace_id;


    public String getStatus() {
        return this.status;
    }

    public String getErrstr() {
        return this.errstr;
    }

    public T getResult() {
        return this.result;
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setErrstr(final String errstr) {
        this.errstr = errstr;
    }

    public void setResult(final T result) {
        this.result = result;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public UcApiResult(final String status, final String errstr, final T result, final String trace_id) {
        this.status = status;
        this.errstr = errstr;
        this.result = result;
        this.trace_id = trace_id;
    }

    public UcApiResult() {
    }

    @Override
    public String toString() {
        return "JoifUcApiResult(status=" + this.getStatus() + ", errstr=" + this.getErrstr() + ", result=" + this.getResult() + ", trcid=" + this.getTrace_id() + ")";
    }
}
