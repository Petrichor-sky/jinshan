package com.haizhi.empower.entity.vo;

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
 * 研判中心-首页待办任务vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "JudgeTaskHomePagePendVo", description = "研判中心-首页待办任务vo")
public class JudgeTaskHomePagePendVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 研判任务主键
     */
    @ApiModelProperty("研判任务主键")
    private Integer id;
    /**
     * 研判任务编号
     */
    @ApiModelProperty("研判任务编号")
    private String taskNo;
    /**
     * 研判任务名称
     */
    @ApiModelProperty("研判任务名称")
    private String taskName;
    /**
     * 研判任务描述
     */
    @ApiModelProperty("研判任务描述")
    private String taskDesciption;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人姓名")
    private String createUser;
    /**
     * 创建单位
     */
    @ApiModelProperty("创建单位")
    private String createUnit;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 任务状态: 1为已完成 0为未完成
     */
    @ApiModelProperty("任务状态: 1为已完成 0为未完成")
    private String taskStatus;
}
