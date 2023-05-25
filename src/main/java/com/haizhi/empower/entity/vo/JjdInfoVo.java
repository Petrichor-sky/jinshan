package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuhongyang
 * @date 2023/4/6 11:44
 * @description 接警单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "JjdInfo", description = "研判中心-接警信息")
public class JjdInfoVo {
    /**
     * 接警编号
     */
    @ApiModelProperty("接警编号")
    private String jjbh;

    /**
     * 报警人
     */
    @ApiModelProperty("报警人")
    private String bjr;

    /**
     * 报警号码
     */
    @ApiModelProperty("报警号码")
    private String bjrlxdh;

    /**
     * 报警时间
     */
    @ApiModelProperty("报警时间")
    private String bjsj;

    /**
     * 接警单位
     */
    @ApiModelProperty("接警单位")
    private String jjdw;

    /**
     * 报警内容
     */
    @ApiModelProperty("报警内容")
    private String bjnr;

    /**
     * 报警地址
     */
    @ApiModelProperty("报警地址")
    private String jjsfdz;

    /**
     * 警情类型
     */
    @ApiModelProperty("接警类型")
    private String jjlxmc;
}

