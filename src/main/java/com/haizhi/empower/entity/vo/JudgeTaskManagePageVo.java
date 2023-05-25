package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Collection;

/**
 * 研判中心-研判任务管理分页vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "JudgeTaskManagePageVo", description = "研判中心-研判任务管理分页vo")
public class JudgeTaskManagePageVo<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 总页数
     */
    @ApiModelProperty("总页数")
    private int pages;
    /**
     * 总条数
     */
    @ApiModelProperty("总条数")
    private long total;
    /**
     * 任务数据
     */
    @ApiModelProperty("任务数据")
    private Collection<T> data;
    /**
     * 进行中的任务总数
     */
    @ApiModelProperty("进行中的任务总数")
    private Integer progressCount;
    /**
     * 已完成的任务总数
     */
    @ApiModelProperty("已完成的任务总数")
    private Integer finishCount;
    /**
     * 我创建的任务总数
     */
    @ApiModelProperty("我创建的任务总数")
    private Integer myCreateCount;
}
