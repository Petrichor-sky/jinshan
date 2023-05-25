package com.haizhi.empower.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/27
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleBo {
    public String id;
    public String roleName;
    public String description;
    public List<Long> permissions;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermissionBo {
        public Long permissionId;
        public String permissionName;
    }
    @Builder
    @AllArgsConstructor

    @Data
    @NoArgsConstructor
    public static class BatchUsersBo {
        public Long roleId;
        public List<String> userCodes;
    }
}

