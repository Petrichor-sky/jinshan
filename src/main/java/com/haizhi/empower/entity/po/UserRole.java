package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 用户角色表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_role")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    //递增主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //角色主键
    @Column(name = "role_id")
    private Long roleId;
    //用户唯一标识
    @Column(name = "user_code")
    private String userCode;
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
