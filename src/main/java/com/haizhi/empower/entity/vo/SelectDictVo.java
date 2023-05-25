package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by fuhanchao
 * @date 2022/11/9.
 */
@Data
public class SelectDictVo {
    @ApiModelProperty(value = "类型码")
    private String code;
    @ApiModelProperty(value = "名称")
    private String name;
}
