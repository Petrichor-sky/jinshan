package com.haizhi.empower.base.resp;

import java.util.Collection;
import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/3/23
 * @Copyright (c) 2009-2022 All rights reserved.
 */
public class TableResultResponse<T> extends BaseResponse {

    int pages;
    long total;

    Collection<T> data;

    public TableResultResponse(int pages,long total, Collection<T> data) {
        this.pages = pages;
        this.total = total;
        this.data = data;
    }
    public TableResultResponse(long total, Collection<T> data) {
        this.total = total;
        this.data = data;
    }

    public TableResultResponse(Collection<T> data) {
        this.data = data;
    }

    public TableResultResponse() {
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Collection<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
