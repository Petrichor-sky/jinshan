package com.haizhi.empower.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/28
 * @Copyright (c) 2009-2022 All rights reserved.
 */
public class PermissionVo {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermissionTreeVo {
        private Long id;

        //菜单名称
        private String name;

        //菜单编码
        private String code;

        //层级
        private Integer level;

        //菜单类型0目录(第1级)、1菜单(第2级)
        private Integer resType;

        //上级菜单code，null无上级
        private String superiorCode;

        //路由类型，0-动态路由(内部路由)、1-静态路由
        private Integer routeType;

        //路由地址
        private String resRoute;

        //链接方式，0-当前窗口、1-新开窗口
        private Integer linkType;

        //备注信息
        private String resDesc;

        //排序
        private Integer itemOrder;

        //1-启用0-停用
        private Integer status;

        //0-系统内置,1-新建的
        private Integer builtIn;

        private String iconUrl;

        /**
         * 子集菜单（权限）
         */
        private List<PermissionTreeVo> subs;
    }
}
