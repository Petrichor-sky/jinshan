package com.haizhi.empower.entity.bo;

import com.haizhi.empower.entity.po.JudgeTaskAttachment;
import com.haizhi.empower.entity.po.JudgeTaskConclusionAttachment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/3/31
 */
@Data
@ApiModel("研判任务")
public class JudgeTaskBo{
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
    private Map taskMind;

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

    @ApiModelProperty("协作者列表")
    private List<JudgeTaskCollaboratorBo> judgeTaskCollaboratorBos;

    @ApiModelProperty("关联要素列表")
    private List<JudgeTaskEventBo> judgeTaskEventBos;

    @ApiModelProperty("附件列表")
    private List<JudgeTaskAttachment> judgeTaskAttachments;

    @ApiModelProperty("结论附件列表")
    private List<JudgeTaskConclusionAttachment> judgeTaskConclusionAttachments;
}
