package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 用户表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_info")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    //递增主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //用户唯一标识
    @Column(name = "user_code")
    private String userCode;
    //用户登录名
    @Column(name = "user_name")
    private String userName;
    //身份证号
    @Column(name = "id_card")
    private String idCard;
    //警号
    @Column(name = "cop_code")
    private String copCode;
    //用户显示名称
    @Column(name = "name")
    private String name;
    //性别
    @Column(name = "sex")
    private String sex;
    //职位
    @Column(name = "position")
    private Integer position;
    //密码
    @Column(name = "password")
    private String password;
    //电子邮件
    @Column(name = "email")
    private String email;
    //手机号
    @Column(name = "mobile")
    private String mobile;
    //头像
    @Column(name = "user_picture")
    private String userPicture;
    //备用号码
    @Column(name = "backup_mobile")
    private String backupMobile;
    //用户在用户中心中的状态
    @Column(name = "is_frozen")
    private Integer isFrozen;
    //用户状态
    @Column(name = "status")
    private Integer status;
    //更新时间
    @Column(name = "update_time")
    private Date updateTime;
    //创建时间
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "dr")
    private Integer dr;
    //用户中心id
//    private String userId;
}
