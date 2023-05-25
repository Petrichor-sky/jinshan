package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/4/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="t_judge_task_attachment")
public class JudgeTaskAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 表主键
     */
    @Column(name="table_id")
    private Integer tableId;

    /**
     * 表名称
     */
    @Column(name="table_name")
    private String tableName;

    /**
     * 附件表主键
     */
    @Column(name="attachment_id")
    private Long attachmentId;

    /**
     * 删除标志(1:删除0:未删除)
     */
    @Column(name="dr")
    private Integer dr;

}
