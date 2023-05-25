package com.haizhi.empower.entity.bo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/28
 * @Copyright (c) 2009-2022 All rights reserved.
 */

public class PermissionBo {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifiedBo {
        private Long id;
        //菜单名称
        @NotEmpty(message = "菜单名称不能为空")
        private String name;

        //菜单类型0目录、1菜单
        @Min(0)
        @Max(1)
        @NotNull(message = "需指定创建类型")
        private Integer resType;

        //上级菜单code，null无上级
        private String superiorCode;

        //路由类型，0-内部路由、1-外部路由
        @Min(0)
        @Max(1)
        private Integer routeType;

        //路由地址
        private String resRoute;

        //链接方式，0-当前窗口、1-新开窗口
        private Integer linkType;

        //备注信息
        private String resDesc;

        private Integer icon;
    }
}
