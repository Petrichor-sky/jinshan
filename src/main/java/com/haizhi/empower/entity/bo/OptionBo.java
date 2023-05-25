package com.haizhi.empower.entity.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by fuhanchao
 * @date 2Ap/1/18.
 */
@Data
public class OptionBo {

    @ApiModelProperty("选项id")
    private String id;

    @ApiModelProperty("选项名称")
    private String name;

    @ApiModelProperty("选项图片地址")
    private String url;

    @ApiModelProperty("选项排序")
    private Integer sortNo;


}
