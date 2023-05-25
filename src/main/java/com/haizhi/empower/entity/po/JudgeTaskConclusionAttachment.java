package com.haizhi.empower.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/4/4
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="t_judge_task_conclusion_attachment")
@ApiModel("研判任务结论附件表")
public class JudgeTaskConclusionAttachment implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 表主键
     */
    @ApiModelProperty("表主键")
    @Column(name="task_id")
    private Integer taskId;

    /**
     * 附件表主键
     */
    @ApiModelProperty("附件表主键")
    @Column(name="attachment_id")
    private Long attachmentId;

    /**
     * 删除标志(1:删除0:未删除)
     */
    @ApiModelProperty("删除标志(1:删除0:未删除)")
    @Column(name="dr")
    private Integer dr;
}
