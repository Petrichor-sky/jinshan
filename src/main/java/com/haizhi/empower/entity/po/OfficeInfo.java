package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 组织信息表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-12 17:38:46
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "office_info")
public class OfficeInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //分组名称
    @Column(name = "office_name")
    private String officeName;
    //分组code
    @Column(name = "office_code")
    private String officeCode;
    //父分组code
    @Column(name = "superior_office_code")
    private String superiorOfficeCode;
    //组织机构代码
    @Column(name = "org_code")
    private String orgCode;
    //是否为临时组织(0:是；1:否)
    @Column(name = "is_temp")
    private Integer isTemp;
    //分组层级
    @Column(name = "level")
    private Integer level;
    //行政区划
    @Column(name = "administrative")
    private Integer administrative;
    //组织类型
    @Column(name = "office_type")
    private Integer officeType;
    //警种
    @Column(name = "suppression")
    private String suppression;
    //删除标志(0:未删除,1:删除)
    @Column(name = "dr")
    private Integer dr;
    //更新时间
    @Column(name = "update_time")
    private Date updateTime;
    //创建时间
    @Column(name = "create_time")
    private Date createTime;

    // 重写hashcode方法
    @Override
    public int hashCode() {
        int result = officeCode.hashCode();
        return result;
    }

    // 重写equals方法
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof OfficeInfo)) {
            // instanceof 已经处理了obj = null的情况
            return false;
        }
        OfficeInfo officeInfo = (OfficeInfo) obj;
        // 地址相等
        if (this == officeInfo) {
            return true;
        }
        // 如果两个对象姓名、年龄、性别相等，我们认为两个对象相等
        if (officeInfo.officeCode.equals(this.officeCode)) {
            return true;
        } else {
            return false;
        }
    }
}
