package com.haizhi.empower.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/12
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleVo {
    public String roleId;
    public String roleName;
    public Integer status;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDetailVo {
        public String roleId;
        public String roleName;
        public String desc;
        public Integer type;
        List<RolePermissionVo> rolePermissionVos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RolePermissionVo {
        public Integer permissionId;
        public String permissionName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleSelectVo{
        private Long value;

        //专题类型名称
        private String label;
    }
}
