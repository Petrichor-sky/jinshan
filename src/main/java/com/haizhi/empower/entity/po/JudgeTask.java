package com.haizhi.empower.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/3/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="t_judge_task")
@ApiModel("研判任务")
public class JudgeTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    @Column(name="task_no")
    private String taskNo;

    /**
     * 研判任务名称
     */
    @ApiModelProperty("研判任务名称")
    @Column(name="task_name")
    private String taskName;

    /**
     * 业务类别，字典表中的judge_task_type
     */
    @ApiModelProperty("业务类别，字典表中的judge_task_type")
    @Column(name="task_type")
    private String taskType;

    /**
     * 任务描述
     */
    @ApiModelProperty("任务描述")
    @Column(name="task_desciption")
    private String taskDesciption;

    /**
     * 研判脑图
     */
    @ApiModelProperty("研判脑图")
    @Column(name="task_mind")
    private String taskMind;

    /**
     * 任务状态，字典judge_task_status
     */
    @ApiModelProperty("任务状态，字典judge_task_status")
    @Column(name="task_status")
    private String taskStatus;

    /**
     * 研判结论
     */
    @ApiModelProperty("研判结论")
    @Column(name="conclusion")
    private String conclusion;

    /**
     * 创建单位
     */
    @ApiModelProperty("创建单位")
    @Column(name="create_unit")
    private String createUnit;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @Column(name="create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @Column(name="create_time")
    private Date createTime;

    //删除标志(1:删除0:未删除)
    @Column(name = "dr")
    private Integer dr;
}
