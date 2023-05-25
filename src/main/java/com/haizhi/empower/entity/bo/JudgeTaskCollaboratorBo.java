package com.haizhi.empower.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/3/31
 */
@Data
@ApiModel("研判任务协作者")
public class JudgeTaskCollaboratorBo {
    /**
     * 协作者
     */
    @ApiModelProperty("协作者")
    @Column(name="collaborator")
    private List<String> collaborator;
}
