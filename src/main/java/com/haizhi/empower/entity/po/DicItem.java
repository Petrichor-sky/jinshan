package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * 字典子表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-27 11:53:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dic_item")
public class DicItem implements Serializable {
    private static final long serialVersionUID = 1L;

    //递增主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //字典编码
    @Column(name = "code")
    private String code;
    //字典项名称
    @Column(name = "name")
    private String name;
    //字典主项编码
    @Column(name = "parent_code")
    private String parentCode;
    //描述
    @Column(name = "description")
    private String description;
    //状态，0禁用1启用
    @Column(name = "status")
    private Integer status;
    //是否为系统字典，0否1是
    @Column(name = "sys_dic")
    private Integer sysDic;
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
