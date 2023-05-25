package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author by fuhanchao
 * @date 2022/12/9.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_apk_version")
public class TbApkVersion implements Serializable {
    public static final long serialVersionUID =1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "apk_name")
    private String apkName;

    @Column(name = "apk_code")
    private String apkCode;

    @Column(name = "apk_version")
    private Integer apkVersion;

    @Column(name = "apk_version_name")
    private String apkVersionName;

    @Column(name = "remark")
    private String remark;

    @Column(name = "del")
    private Integer del;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;


    public TbApkVersion (String apkCode){
        this.apkCode = apkCode;
    }



}
