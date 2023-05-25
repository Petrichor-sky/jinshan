package com.haizhi.empower.entity.bo;

import com.haizhi.empower.entity.po.JudgeTaskAttachment;
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
@ApiModel("研判任务协同")
public class JudgeTaskCoordinationBo {

    @ApiModelProperty("任务id")
    @Column(name="task_id")
    private Integer taskId;

    /**
     * 新建的协调内容为0，回复的内容为上一级的id
     */
    @ApiModelProperty("新建的协调内容为0，回复的内容为上一级的id")
    @Column(name="parent_id")
    private Integer parentId;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    @Column(name="content")
    private String content;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人唯一标识")
    @Column(name="create_user")
    private String createUser;

    /**
     * 创建人单位
     */
    @ApiModelProperty("创建人单位唯一标识")
    @Column(name="create_unit")
    private String createUnit;

    /**
     * 回复人姓名
     */
    @ApiModelProperty("回复人唯一标识")
    @Column(name="reply_name")
    private String replyName;

    @ApiModelProperty("附件列表")
    private List<JudgeTaskAttachment> judgeTaskAttachments;
}
