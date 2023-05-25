package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 角色权限信息
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-28 15:48:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role_permission")
public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //权限主键
    @Column(name = "permission_id")
    private Long permissionId;
    //角色主键
    @Column(name = "role_id")
    private String roleId;
    //授权类型 0：仅可见；1：可见并可用
    @Column(name = "authorization_type")
    private Integer authorizationType;
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
