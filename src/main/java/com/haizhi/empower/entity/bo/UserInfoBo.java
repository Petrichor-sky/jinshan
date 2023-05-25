package com.haizhi.empower.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/6
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
public class UserInfoBo {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginBo {
        @NotEmpty(message = "登录用户不能为空")
        public String userName;
        @NotEmpty(message = "登录密码不能为空")
        public String password;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAppLoginBo{
//        @NotEmpty(message = "登录用户不能为空")
        public String username;
//        @NotEmpty(message = "企业域不能为空")
        public String domain;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangePasswordBo {
        @NotEmpty(message = "用户不能为空")
        public String userCode;
        @NotEmpty(message = "组织不能为空")
        public String officeCode;
        public Boolean defaultOffice;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddBo {
        public Integer id;
        @NotEmpty(message = "账号不能为空")
        public String userName;
        @NotEmpty(message = "姓名不能为空")
        public String name;
        @NotEmpty(message = "警号不能为空")
        public String copCode;
        @NotNull(message = "职位不能为空")
        public Integer position;
        @NotEmpty(message = "身份证号不能为空")
        public String idCard;
        @NotEmpty(message = "性别不能为空")
        public String sex;
        @NotNull(message = "状态不能为空")
        public Integer status;
        @NotNull(message = "所属角色不能为空")
        private List<Integer> roleIds;
        @NotNull(message = "所属组织不能为空")
        public List<String> officeCodes;

        //头像
        private String userPicture;
    }

}
