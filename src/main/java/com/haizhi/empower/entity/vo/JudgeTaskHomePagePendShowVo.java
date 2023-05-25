package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 研判中心-首页待办任务展示vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "JudgeTaskHomePagePendShowVo", description = "研判中心-首页待办任务展示vo")
public class JudgeTaskHomePagePendShowVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 待办任务数量
     */
    @ApiModelProperty("首页展示的待办任务数量")
    private int pendCount;
    /**
     * 首页展示的待办任务
     */
    @ApiModelProperty("首页展示的待办任务")
    private List<JudgeTaskHomePagePendVo> pendList;
}
