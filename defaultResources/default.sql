SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `size`        bigint(20) DEFAULT NULL COMMENT '附件大小',
    `type`        tinyint(4) DEFAULT NULL COMMENT '附件类型 1-图片、2-视频、3-附件',
    `name`        varchar(255) NOT NULL COMMENT '附件名',
    `ext_name`    varchar(255) NOT NULL COMMENT '附件拓展名',
    `address`     varchar(255) NOT NULL COMMENT '附件地址',
    `dr`          smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='附件管理表';

-- ----------------------------
-- Records of attachment
-- ----------------------------
BEGIN;
INSERT INTO `attachment` (`id`, `size`, `type`, `name`, `ext_name`, `address`, `dr`, `update_time`, `create_time`)
VALUES (1, 18261, 3, '0c7c208baf4012ebcda40b9915c94c84', 'jpeg', 'form-data/739f90364ce757790d1b6b9ddab253e9.jpeg', 0,
        '2022-09-16 16:16:12', '2022-09-16 16:16:12');
COMMIT;

-- ----------------------------
-- Table structure for dic_item
-- ----------------------------
DROP TABLE IF EXISTS `dic_item`;
CREATE TABLE `dic_item`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '递增主键',
    `code`        varchar(50)  NOT NULL COMMENT '字典编码',
    `name`        varchar(50)  NOT NULL COMMENT '字典项名称',
    `parent_code` varchar(128) NOT NULL COMMENT '字典主项编码',
    `description` varchar(512)          DEFAULT NULL COMMENT '描述',
    `status`      tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态，0禁用1启用',
    `sys_dic`     tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为系统字典，0否1是',
    `dr`          smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='字典子表';

-- ----------------------------
-- Table structure for dic_type
-- ----------------------------
DROP TABLE IF EXISTS `dic_type`;
CREATE TABLE `dic_type`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`        varchar(255) NOT NULL COMMENT '字典名称',
    `code`        varchar(255) NOT NULL COMMENT '字典代码',
    `sys_dic`     tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为系统字典，0是1否',
    `description` varchar(500)          DEFAULT NULL COMMENT '描述',
    `dr`          smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='字典表';

-- ----------------------------
-- Records of dic_type
-- ----------------------------
BEGIN;
INSERT INTO `dic_type` (`id`, `name`, `code`, `sys_dic`, `description`, `dr`, `create_time`, `update_time`)
VALUES (1, '警种', 'police_category', 0, '警种类型描述', 0, '2022-04-26 18:38:04', '2022-04-26 18:38:04');
INSERT INTO `dic_type` (`id`, `name`, `code`, `sys_dic`, `description`, `dr`, `create_time`, `update_time`)
VALUES (2, '行政区划', 'administrative', 0, '行政区划', 0, '2022-04-29 14:42:16', '2022-04-29 14:42:16');
INSERT INTO `dic_type` (`id`, `name`, `code`, `sys_dic`, `description`, `dr`, `create_time`, `update_time`)
VALUES (3, '职位', 'police_position', 0, '警察职位', 0, '2022-05-06 14:05:24', '2022-05-06 14:05:24');
COMMIT;

-- ----------------------------
-- Table structure for office_info
-- ----------------------------
DROP TABLE IF EXISTS `office_info`;
CREATE TABLE `office_info`
(
    `id`                   int(11) NOT NULL AUTO_INCREMENT,
    `office_name`          varchar(255) DEFAULT NULL COMMENT '分组名称',
    `office_code`          varchar(50)  DEFAULT NULL COMMENT '分组code',
    `superior_office_code` varchar(50)  DEFAULT NULL COMMENT '父分组code',
    `is_temp`              tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否为临时组织(0:是；1:否)',
    `level`                smallint(6) unsigned NOT NULL DEFAULT '0' COMMENT '分组层级',
    `administrative`       smallint(6) DEFAULT NULL COMMENT '行政区划',
    `office_type`          smallint(6) DEFAULT NULL COMMENT '组织类型：0-警种组织，1-行政组织',
    `suppression`          varchar(100) DEFAULT NULL COMMENT '警种',
    `dr`                   smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(0:未删除,1:删除)',
    `update_time`          datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time`          datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                    `office_code` (`office_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='组织信息表';

-- ----------------------------
-- Records of office_info
-- ----------------------------
BEGIN;
INSERT INTO `office_info` (`id`, `office_name`, `office_code`, `superior_office_code`, `is_temp`, `level`,
                           `administrative`, `office_type`, `suppression`, `dr`, `update_time`, `create_time`)
VALUES (1, '全局', '5YWo5YWs5Y4', '', 1, 0, 1, 1, '[46]', 0, '2022-09-15 17:42:18', '2022-04-13 15:29:43');
COMMIT;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          varchar(50)  DEFAULT NULL COMMENT '菜单名称',
    `code`          varchar(50) NOT NULL COMMENT '菜单编码',
    `level`         smallint(6) DEFAULT NULL COMMENT '层级',
    `res_type`      tinyint(1) DEFAULT NULL COMMENT '菜单类型0目录、1菜单',
    `superior_code` varchar(50)  DEFAULT NULL COMMENT '上级菜单code，null无上级',
    `route_type`    tinyint(1) DEFAULT NULL COMMENT '路由类型，0-动态路由(内部路由)、1-静态路由',
    `res_route`     varchar(512) DEFAULT NULL COMMENT '路由地址',
    `link_type`     tinyint(1) DEFAULT NULL COMMENT '链接方式，0-当前窗口、1-新开窗口',
    `res_desc`      varchar(128) DEFAULT NULL COMMENT '备注信息',
    `icon`          smallint(6) DEFAULT NULL COMMENT '图标',
    `built_in`      tinyint(1) NOT NULL COMMENT '内置菜单\\目录：0-内置，1-新增的',
    `item_order`    int(11) DEFAULT NULL COMMENT '排序',
    `status`        tinyint(1) NOT NULL COMMENT '状态1启用0停用',
    `dr`            tinyint(1) NOT NULL COMMENT '启用状态(1:删除0:未删除)',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单(权限)信息表';

-- ----------------------------
-- Records of permission
-- ----------------------------
BEGIN;
INSERT INTO `permission` (`id`, `name`, `code`, `level`, `res_type`, `superior_code`, `route_type`, `res_route`,
                          `link_type`, `res_desc`, `icon`, `built_in`, `item_order`, `status`, `dr`, `update_time`,
                          `create_time`)
VALUES (1, '系统管理', 'catalogue_xtgl', 1, 0, '', NULL, NULL, 0, '系统管理目录', 1, 0, 4, 1, 0, '2022-08-04 17:01:11',
        '2022-05-12 15:38:00');
INSERT INTO `permission` (`id`, `name`, `code`, `level`, `res_type`, `superior_code`, `route_type`, `res_route`,
                          `link_type`, `res_desc`, `icon`, `built_in`, `item_order`, `status`, `dr`, `update_time`,
                          `create_time`)
VALUES (2, '组织用户管理', 'menu_zzyhgl', 2, 1, 'catalogue_xtgl', 0, '/#/manage/user', 0, '组织用户管理菜单', 1, 0, 1, 1, 0,
        '2022-08-04 17:01:10', '2022-05-13 14:30:53');
INSERT INTO `permission` (`id`, `name`, `code`, `level`, `res_type`, `superior_code`, `route_type`, `res_route`,
                          `link_type`, `res_desc`, `icon`, `built_in`, `item_order`, `status`, `dr`, `update_time`,
                          `create_time`)
VALUES (3, '角色管理', 'menu_jsgl', 2, 1, 'catalogue_xtgl', 0, '/#/manage/role', 0, '角色管理菜单', 1, 0, 3, 1, 0,
        '2022-08-04 17:01:10', '2022-05-13 14:32:22');
INSERT INTO `permission` (`id`, `name`, `code`, `level`, `res_type`, `superior_code`, `route_type`, `res_route`,
                          `link_type`, `res_desc`, `icon`, `built_in`, `item_order`, `status`, `dr`, `update_time`,
                          `create_time`)
VALUES (4, '菜单管理', 'menu_cdgl', 2, 1, 'catalogue_xtgl', 0, '/#/manage/menu', 0, '菜单管理菜单', 1, 0, 5, 1, 0,
        '2022-08-04 17:01:10', '2022-05-13 14:39:32');
INSERT INTO `permission` (`id`, `name`, `code`, `level`, `res_type`, `superior_code`, `route_type`, `res_route`,
                          `link_type`, `res_desc`, `icon`, `built_in`, `item_order`, `status`, `dr`, `update_time`,
                          `create_time`)
VALUES (5, '字典管理', 'menu_zdgl', 2, 1, 'catalogue_xtgl', 0, '/#/manage/dictionary', 0, '字典管理菜单1', 1, 0, 6, 1, 0,
        '2022-08-04 17:01:10', '2022-05-13 14:40:01');
COMMIT;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_name`   varchar(255) DEFAULT NULL COMMENT '角色名称',
    `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
    `role_type`   tinyint(3) unsigned NOT NULL DEFAULT '2' COMMENT '角色类型，0超管，1管理角色，2民警',
    `status`      tinyint(4) DEFAULT '1' COMMENT '状态：1开启；0关闭',
    `sys_role`    tinyint(4) DEFAULT NULL COMMENT '是否是系统内置角色：0-否，1-是(系统内置角色不可删除)',
    `level`       tinyint(4) DEFAULT '0' COMMENT '等级',
    `dr`          tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='角色信息';

-- ----------------------------
-- Records of role
-- ----------------------------
BEGIN;
INSERT INTO `role` (`id`, `role_name`, `description`, `role_type`, `status`, `sys_role`, `level`, `dr`, `update_time`,
                    `create_time`)
VALUES (1, '超级管理员', '超级管理员有超级权限', 0, 1, 1, 0, 0, '2022-09-14 16:07:56', '2022-03-30 15:48:10');
COMMIT;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`
(
    `id`                 bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `permission_id`      bigint(20) unsigned NOT NULL COMMENT '权限主键',
    `role_id`            bigint(20) unsigned NOT NULL COMMENT '角色主键',
    `authorization_type` tinyint(3) unsigned DEFAULT NULL COMMENT '授权类型 0：仅可见；1：可见并可用',
    `dr`                 tinyint(3) unsigned NOT NULL COMMENT '删除标志(1:删除0:未删除)',
    `update_time`        datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time`        datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色权限信息';

-- ----------------------------
-- Records of role_permission
-- ----------------------------
BEGIN;
INSERT INTO `role_permission` (`id`, `permission_id`, `role_id`, `authorization_type`, `dr`, `update_time`,
                               `create_time`)
VALUES (1, 1, 1, 1, 1, '2022-08-22 17:22:19', '2022-08-22 17:22:19');
INSERT INTO `role_permission` (`id`, `permission_id`, `role_id`, `authorization_type`, `dr`, `update_time`,
                               `create_time`)
VALUES (2, 1, 1, 1, 1, '2022-08-22 17:22:19', '2022-08-22 17:22:19');
INSERT INTO `role_permission` (`id`, `permission_id`, `role_id`, `authorization_type`, `dr`, `update_time`,
                               `create_time`)
VALUES (3, 1, 2, 1, 1, '2022-08-22 17:22:19', '2022-08-22 17:22:19');
INSERT INTO `role_permission` (`id`, `permission_id`, `role_id`, `authorization_type`, `dr`, `update_time`,
                               `create_time`)
VALUES (4, 1, 2, 1, 1, '2022-08-22 17:22:19', '2022-08-22 17:22:19');
INSERT INTO `role_permission` (`id`, `permission_id`, `role_id`, `authorization_type`, `dr`, `update_time`,
                               `create_time`)
VALUES (5, 1, 2, 1, 1, '2022-08-22 17:22:19', '2022-08-22 17:22:19');
COMMIT;


-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '递增主键',
    `user_code`     varchar(50)  DEFAULT NULL COMMENT '用户唯一标识',
    `user_name`     varchar(100) DEFAULT NULL COMMENT '用户登录名',
    `id_card`       varchar(50)  DEFAULT NULL COMMENT '身份证号',
    `cop_code`      varchar(30)  DEFAULT NULL COMMENT '警号',
    `name`          varchar(50)  NOT NULL COMMENT '用户名称',
    `sex`           varchar(5)   NOT NULL COMMENT '性别',
    `position`      smallint(6) NOT NULL COMMENT '职位',
    `password`      varchar(100) NOT NULL COMMENT '密码',
    `email`         varchar(80)  DEFAULT NULL COMMENT '电子邮件',
    `mobile`        varchar(20)  DEFAULT NULL COMMENT '手机号',
    `backup_mobile` varchar(20)  DEFAULT NULL COMMENT '备用号码',
    `is_frozen`     smallint(6) DEFAULT NULL COMMENT '用户在用户中心中的状态',
    `status`        smallint(6) NOT NULL DEFAULT '0' COMMENT '用户状态(0-使用，1-禁用)',
    `dr`            smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `user_picture`  varchar(100) DEFAULT NULL COMMENT '用户投降地址',
    PRIMARY KEY (`id`) USING BTREE,
    KEY             `user_code` (`user_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户表';

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` (`id`, `user_code`, `user_name`, `id_card`, `cop_code`, `name`, `sex`, `position`, `password`,
                         `email`, `mobile`, `backup_mobile`, `is_frozen`, `status`, `dr`, `update_time`, `create_time`,
                         `user_picture`)
VALUES (1, 'u_62eb84a127ddbd118f1ec4b5', 'admin', '420102197812341234', '024025', '超管', '男', 52,
        'pbkdf2:sha1:1000$SjlUFEPG$a373eec5832516f24dfe2af113d09d37248fc3ef', NULL, NULL, NULL, NULL, 0, 0,
        '2022-09-06 11:43:09', '2022-08-04 16:34:42',
        'http://124.222.12.212:80/minio_res/self-task/e490b56e23bb60151f45dac1633a8e9c.jpg');
COMMIT;

-- ----------------------------
-- Table structure for user_office
-- ----------------------------
DROP TABLE IF EXISTS `user_office`;
CREATE TABLE `user_office`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '递增主键',
    `office_code` varchar(50) NOT NULL COMMENT '组织表主键',
    `user_code`   varchar(50) NOT NULL COMMENT '用户唯一标识',
    `is_default`  smallint(6) NOT NULL DEFAULT '0' COMMENT '是否是默认组织:0-不是，1-是',
    `dr`          smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=353 DEFAULT CHARSET=utf8mb4 COMMENT='用户组织表';

-- ----------------------------
-- Records of user_office
-- ----------------------------
BEGIN;
INSERT INTO `user_office` (`id`, `office_code`, `user_code`, `is_default`, `dr`, `update_time`, `create_time`) VALUES (1, '5YWo5YWs5Y4', 'u_62eb84a127ddbd118f1ec4b5', 1, 1, '2022-08-30 11:17:39', '2022-08-04 16:40:06');
COMMIT;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '递增主键',
    `role_id`     bigint(20) NOT NULL COMMENT '角色主键',
    `user_code`   varchar(50) NOT NULL COMMENT '用户唯一标识',
    `dr`          smallint(6) NOT NULL DEFAULT '0' COMMENT '删除标志(1:删除0:未删除)',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `user_code` (`user_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=298 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户角色表';

-- ----------------------------
-- Records of user_role
-- ----------------------------
BEGIN;
INSERT INTO `user_role` (`id`, `role_id`, `user_code`, `dr`, `update_time`, `create_time`) VALUES (1, 1, 'u_62eb84a127ddbd118f1ec4b5', 1, '2022-08-30 11:17:39', '2022-08-04 16:41:19');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
