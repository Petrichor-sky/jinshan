package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * 菜单(权限)信息表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-28 15:48:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //菜单名称
    @Column(name = "name")
    private String name;
    //菜单编码
    @Column(name = "code")
    private String code;
    //层级
    @Column(name = "level")
    private Integer level;
    //菜单类型0目录、1菜单
    @Column(name = "res_type")
    private Integer resType;
    //上级菜单code，null无上级
    @Column(name = "superior_code")
    private String superiorCode;
    //路由类型，0-动态路由(内部路由)、1-静态路由
    @Column(name = "route_type")
    private Integer routeType;
    //路由地址
    @Column(name = "res_route")
    private String resRoute;
    //链接方式，0-当前窗口、1-新开窗口
    @Column(name = "link_type")
    private Integer linkType;
    //备注信息
    @Column(name = "res_desc")
    private String resDesc;
    //排序
    @Column(name = "item_order")
    private Integer itemOrder;
    //状态1启用0停用
    @Column(name = "status")
    private Integer status;
    //启用状态(1:删除0:未删除)
    @Column(name = "dr")
    private Integer dr;
    //更新时间
    @Column(name = "update_time")
    private Date updateTime;
    //创建时间
    @Column(name = "create_time")
    private Date createTime;
    //内置菜单\目录：0-内置，1-新增的
    @Column(name = "built_in")
    private Integer builtIn;

    @Column(name = "icon")
    private Integer icon;
}
