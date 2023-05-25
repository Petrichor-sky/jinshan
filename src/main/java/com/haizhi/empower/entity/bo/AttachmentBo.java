package com.haizhi.empower.entity.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentBo {
    @ApiModelProperty(value = "附件id")
    private Long attachmentId;
}
