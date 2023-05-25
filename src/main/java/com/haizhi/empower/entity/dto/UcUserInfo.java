package com.haizhi.empower.entity.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户中心用户信息
 *
 * @author ：lvchengfei
 * @date ：2023/4/7 9:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UcUserInfo  implements Serializable {

    private static final long serialVersionUID = 5381930837060596066L;

    //身份证号码
    private String card_id;

    //邮箱
    private String email;

    //手机号
    private String mobile;

    //用户显示名称
    private String name;

    //性别
    private String sex;

    //用户标识
    private String user_id;

    private String username;

    private Integer status;

    //所属组织机构
    private List<UcUserInfo.Group> group_list;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Group implements Serializable {
        //原生组织机构编码
        private String org_code;
        //组织名称
        private String group_name;
        //机构标识
        private String group_id;
    }
}
