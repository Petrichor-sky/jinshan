package com.haizhi.empower.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 研判中心-研判任务管理分页dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "JudgeTaskManagePageDto", description = "研判中心-研判任务管理分页dto")
public class JudgeTaskManagePageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 研判任务主键
     */
    @ApiModelProperty("研判任务主键")
    private Integer id;
    /**
     * 研判任务名称
     */
    @ApiModelProperty("研判任务名称")
    private String taskName;
    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String taskNo;
    /**
     * 任务描述
     */
    @ApiModelProperty("任务描述")
    private String taskDesciption;
    /**
     * 任务状态: 1为已完成 0为未完成
     */
    @ApiModelProperty("任务状态: 1为已完成 0为未完成")
    private String taskStatus;
    /**
     * 创建单位名称
     */
    @ApiModelProperty("创建单位名称")
    private String createUnit;
    /**
     * 创建单位唯一标识
     */
    @ApiModelProperty("创建单位唯一标识")
    private String officeCode;
    /**
     * 创建人姓名
     */
    @ApiModelProperty("创建人姓名")
    private String createUser;
    /**
     * 创建人唯一标识
     */
    @ApiModelProperty("创建人唯一标识")
    private String userCode;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 当前登录人,是否是该任务的创建人
     */
    @ApiModelProperty("当前登录人,是否是该任务的创建人: 1为是 0为否")
    private int loginUserType;
}
