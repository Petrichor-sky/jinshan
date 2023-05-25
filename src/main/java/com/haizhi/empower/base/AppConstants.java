package com.haizhi.empower.base;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/3/23
 * @Copyright (c) 2009-2022 All rights reserved.
 */
public interface AppConstants {
    /**
     * 默认响应Code与Message
     */
    final class DefaultResponse {
        public static final int TOKEN_ERROR = 302;
        public static final int ERROR = 500;
        public static final int SUCCESS = 200;
        public static final String SUCCESS_MSG = "success";
        public static final String ERROR_MSG = "error";
    }

    final class Key {
        public static final String URI = "URI";
        public static final String TOKEN_HEADER = "access_token";
        public final static String TOKEN_USERCODE_HEADER = "dp_user_id";
        public final static String TOKEN_USERCODE = "user_code";

        public static final String TOKEN_USER_ID = "my_user_id";
    }

    final class Uc {
        public static final String DEV_HEADER = "X-HAIZHI-TOKEN";

        /**
         * 查询用户详细信息
         */
        public static final String USER_INFO = "/api/ucenter/openapi/user/info";

        /**
         * 查询系统角色列表
         */
        public static final String ROLE_LIST = "/api/ucenter/openapi/role/list";
        /**
         * 获取角色信息
         */
        public static final String ROLE_INFO = "/api/ucenter/openapi/role/info";
        /**
         * 获取角色下用户
         */
        public static final String USER_LIST = "/api/ucenter/openapi/role/user_list";

        /**
         * 给角色添加用户
         */
        public static final String USER_ADD = "/api/ucenter/openapi/role/user_add";

        /**
         * 给角色删除用户
         */
        public static final String USER_DEL = "/api/ucenter/openapi/role/user_del";

    }
}
