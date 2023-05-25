package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@Data
public class JudgeTaskMessageVo {
    @ApiModelProperty("消息id")
    private String id;
    @ApiModelProperty("消息类型")
    private String type;
    @ApiModelProperty("消息内容")
    private String content;
}
