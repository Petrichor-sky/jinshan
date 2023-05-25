package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuhongyang
 * @date 2023/4/6 17:56
 * @description 人员信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "PersonVo", description = "研判中心-人员信息")
public class PersonVo {
    @ApiModelProperty("身份证号码")
    private String gmsfzhm;

    @ApiModelProperty("姓名")
    private String xm;

    @ApiModelProperty("性别")
    private String xbMc;

    @ApiModelProperty("民族")
    private String mzMc;

    @ApiModelProperty("血型")
    private String xxMc;

    @ApiModelProperty("身高")
    private String sg;

    @ApiModelProperty("职业")
    private String zy;

    @ApiModelProperty("职务")
    private String zw;

    @ApiModelProperty("文化程度")
    private String whcdMc;

    @ApiModelProperty("婚姻状况")
    private String hyzkMc;

    @ApiModelProperty("户籍地址")
    private String hjdxxdz;

    @ApiModelProperty("现住址")
    private String xzzxxdz;

    @ApiModelProperty("登记派出所")
    private String djpcsmc;

    @ApiModelProperty("电话号码")
    private String dhhm;
}
