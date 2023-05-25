package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * 字典表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dic_type")
public class DicType implements Serializable {
    private static final long serialVersionUID = 1L;

    //编号
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //字典名称
    @Column(name = "name")
    private String name;
    //字典代码
    @Column(name = "code")
    private String code;
    //是否为系统字典，0是1否
    @Column(name = "sys_dic")
    private Integer sysDic;
    //描述
    @Column(name = "description")
    private String description;
    //删除标志(1:删除0:未删除)
    @Column(name = "dr")
    private Integer dr;
    //创建时间
    @Column(name = "create_time")
    private Date createTime;
    //更新时间
    @Column(name = "update_time")
    private Date updateTime;

}
