package com.haizhi.empower.entity.bean.msg;

import lombok.Data;

import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@Data
public class MsgReadBean {
    // 消息列表
    private List<String> msgIds;
    /**
     * 是否已读全部，0 否； 1 是
     */
    private int isAll;
}
