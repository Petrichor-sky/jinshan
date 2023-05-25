package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CristianWindy
 * @date 2022-08-16 15:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentVo implements Serializable {
    @ApiModelProperty("附件主键")
    private String id;

    @ApiModelProperty("附件类型 1-图片、2-视频、3-附件")
    private Integer type;

    @ApiModelProperty("后缀名")
    private String extName;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("地址")
    private String address;
}
