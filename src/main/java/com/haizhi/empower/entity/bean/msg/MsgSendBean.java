package com.haizhi.empower.entity.bean.msg;

import lombok.Data;

import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/3/3.
 */
@Data
public class MsgSendBean {
    private List<String> userIds;
    private List<String> groupIds;
    private String content;
}
