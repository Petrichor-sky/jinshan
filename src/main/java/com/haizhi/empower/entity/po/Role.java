package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * 角色信息
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //角色名称
    @Column(name = "role_name")
    private String roleName;
    //角色描述
    @Column(name = "description")
    private String description;
    //角色类型，0超管，1管理角色，2民警
    @Column(name = "role_type")
    private Integer roleType;
    //状态：1开启；0关闭
    @Column(name = "status")
    private Integer status;
    //是否是系统内置角色：0-否，1-是(系统内置角色不可删除)
    @Column(name = "sys_role")
    private Integer sysRole;
    //等级
    @Column(name = "level")
    private Integer level;
    //删除标志(1:删除0:未删除)
    @Column(name = "dr")
    private Integer dr;
    //更新时间
    @Column(name = "update_time")
    private Date updateTime;
    //创建时间
    @Column(name = "create_time")
    private Date createTime;

}
