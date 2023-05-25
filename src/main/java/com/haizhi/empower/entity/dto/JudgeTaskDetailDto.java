package com.haizhi.empower.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haizhi.empower.entity.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 研判中心-研判任务详情dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "JudgeTaskDetailDto", description = "研判中心-研判任务详情dto")
public class JudgeTaskDetailDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 研判任务主键
     */
    @ApiModelProperty("任务主键")
    private Integer id;
    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String taskName;
    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String taskNo;
    /**
     * 任务状态: 1为已完成 0为未完成
     */
    @ApiModelProperty("任务状态: 1为已完成 0为未完成")
    private String taskStatus;
    /**
     * 任务创建人唯一标识
     */
    @ApiModelProperty("任务创建人唯一标识")
    private String createCode;
    /**
     * 任务创建人姓名
     */
    @ApiModelProperty("任务创建人姓名")
    private String createUser;
    /**
     * 任务创建单位唯一标识
     */
    @ApiModelProperty("任务创建单位唯一标识")
    private String officeCode;
    /**
     * 任务创建单位
     */
    @ApiModelProperty("任务创建单位名称")
    private String createOffice;
    /**
     * 任务创建时间
     */
    @ApiModelProperty("任务创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 任务描述
     */
    @ApiModelProperty("任务描述")
    private String taskDesciption;
    /**
     * 任务研判脑图
     */
    @ApiModelProperty("研判脑图")
    private String taskMind;
    /**
     * 任务结论
     */
    @ApiModelProperty("任务结论")
    private String taskConclusion;
    /**
     * 任务附件
     */
    @ApiModelProperty("任务附件")
    private List<AttachmentVo> taskAttachment;
    /**
     * 结论附件
     */
    @ApiModelProperty("结论附件")
    private List<AttachmentVo> conclusionAttachment;
    /**
     * 任务协作者
     */
    @ApiModelProperty("任务协作者")
    private List<UserVo> taskCollaborator;
    /**
     * 任务关联要素-警情
     */
    @ApiModelProperty("任务关联要素-警情")
    private Map<String,List<JjdInfoVo>> informingMap;
    /**
     * 任务关联要素-案件
     */
    @ApiModelProperty("任务关联要素-案件")
    private Map<String,List<CaseInfoVo>> caseMap;
    /**
     * 任务关联要素-人员
     */
    @ApiModelProperty("任务关联要素-人员")
    private Map<String,List<PersonVo>> manMap;
    /**
     * 任务关联要素-车辆
     */
    @ApiModelProperty("任务关联要素-车辆")
    private Map<String,List<CarVo>> vehicleMap;
    /**
     * 任务一级协同
     */
    @ApiModelProperty("任务一级协同列表")
    private List<JudgeTaskCoordinationVo> taskCoordination;
}
