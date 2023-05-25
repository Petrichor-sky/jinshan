package com.haizhi.empower.entity.bean.msg;

import lombok.Data;

import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */

@Data
public class MsgResultBean {

    private int total;

    private List<MsgListBean> msgList;
    @Data
    public static class MsgListBean {
        private int status;
        private String source_name;
        private String icon_color;
        private String receivers;
        private String type_name;
        private String content;
        private String icon_name;
        private String info_id;
        private int mode;
        private String ctime;
    }

}


