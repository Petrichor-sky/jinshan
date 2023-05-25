package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * 附件管理表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attachment")
public class Attachment implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //附件大小
    @Column(name = "size")
    private Long size;
    //附件类型 1-图片、2-视频、3-附件
    @Column(name = "type")
    private Integer type;
    //附件名
    @Column(name = "name")
    private String name;
    //附件拓展名
    @Column(name = "ext_name")
    private String extName;
    //附件地址
    @Column(name = "address")
    private String address;
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
