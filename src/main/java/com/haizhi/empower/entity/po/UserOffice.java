package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * 用户组织表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-12 17:38:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_office")
public class UserOffice implements Serializable {
    private static final long serialVersionUID = 1L;

    //递增主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //组织表主键
    @Column(name = "office_code")
    private String officeCode;
    //用户唯一标识
    @Column(name = "user_code")
    private String userCode;
    //是否是默认组织:0-不是，1-是
    @Column(name = "is_default")
    private Integer isDefault;
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
