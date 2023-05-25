package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuhongyang
 * @date 2023/4/6 17:42
 * @description 案件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "CaseInfoVo", description = "研判中心-案件信息")
public class CaseInfoVo {
    @ApiModelProperty("案件编号")
    private String ajbh;

    @ApiModelProperty("案件类别")
    private String ajlb;

    @ApiModelProperty("案件名称")
    private String ajmc;

    @ApiModelProperty("立案时间")
    private String lasj;

    @ApiModelProperty("简要案情")
    private String jyaq;

    @ApiModelProperty("案件状态")
    private String ajzt;

    @ApiModelProperty("案件详细地址")
    private String ajxxdzmc;

    @ApiModelProperty("主办人")
    private String zbrxm;

    @ApiModelProperty("主办单位名称")
    private String zbdwmc;

    @ApiModelProperty("受理时间")
    private String slsj;

}
