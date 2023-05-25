package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuhongyang
 * @date 2023/4/6 18:20
 * @description 机动车
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "CarVo", description = "研判中心-车辆信息")
public class CarVo {
    @ApiModelProperty("车牌号")
    private String jdchphm;

    @ApiModelProperty("品牌")
    private String zwppmc;

    @ApiModelProperty("发动机号")
    private String jdcfdjddjh;

    @ApiModelProperty("车辆类型")
    private String jdccllxmc;

    @ApiModelProperty("车身颜色")
    private String jdccsysmc;

    @ApiModelProperty("机动车所有人")
    private String jdcsyr;

    @ApiModelProperty("车辆用途")
    private String clytmc;

    @ApiModelProperty("手机号码")
    private String jdcsyrLxdh;

    @ApiModelProperty("身份证号")
    private String sfzh;


}
