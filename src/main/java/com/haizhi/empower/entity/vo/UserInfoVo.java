package com.haizhi.empower.entity.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/3/24
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoVo {
    private String access_token;
    private String create_time;
    private String group_id;
    private String id;
    private String identity;
    private String is_del;
    private String last_login_time;
    private String name;
    private Integer person_status;
    private String phone;
    private String police_id;
    private Integer sex;
    private Integer status;
    private String sys_role;
    private String username;
    private String groupName;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskHandlerBeanVo {
        private List<TaskHandlerVo> taskHandlerVos;
        private Integer total;
        private Integer matched;
        private Integer unmatched;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskHandlerVo {
        private Integer taskId;
        private Integer type;//0-单位；1-民警

        private List<OfficeVo> officeVo;

        private UserVo userVo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserOfficeVo {
        private Integer id;
        private String userCode;
        private String userName;
        private String name;
        private String copCode;
        private String idCard;
        private String position;
        private String sex;
        private List<RoleVo> roleVos;
        private Integer status;
        private String createTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDetailVo {
        private Long id;
        private String userCode;
        private String userName;
        private String name;
        private String copCode;
        private String idCard;
        private Integer position;
        private String sex;
        private List<RoleVo> roleVos;
        private List<OfficeVo> officeVos;
        private Integer status;
        private String userPicture;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserShortVo {
        public String userCode;
        public String userName;
    }

}
