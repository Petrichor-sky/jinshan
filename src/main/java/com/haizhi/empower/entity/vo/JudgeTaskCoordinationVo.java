package com.haizhi.empower.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/4/4
 */
@Data
@ApiModel("研判任务协同")
public class JudgeTaskCoordinationVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 协同主键
     */
    @ApiModelProperty("协同id")
    private Integer id;

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    @Column(name="task_id")
    private Integer taskId;

    /**
     * 新建的协调内容为0，回复的内容为上一级的id
     */
    @ApiModelProperty("新建的协调内容为0，回复的内容为上一级的id")
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    @Column(name = "content")
    private String content;

    /**
     * 创建人唯一标识
     */
    @ApiModelProperty("创建人唯一标识")
    @Column(name = "create_user")
    private String createUser;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    @Column(name = "create_user")
    private String createUserName;

    /**
     * 创建人警号
     */
    @ApiModelProperty("创建人警号")
    private String copCode;

    /**
     * 创建人单位唯一标识
     */
    @ApiModelProperty("创建人单位唯一标识")
    @Column(name = "create_unit")
    private String createUnit;

    /**
     * 创建人单位
     */
    @ApiModelProperty("创建人单位")
    @Column(name = "create_unit")
    private String createUnitName;

    /**
     * 回复人唯一标识
     */
    @ApiModelProperty("回复人唯一标识")
    @Column(name = "reply_name")
    private String replyName;

    /**
     * 回复人姓名
     */
    @ApiModelProperty("回复人姓名")
    @Column(name = "reply_name")
    private String replyRealName;

    //头像
    private String userPicture;

    /**
     * 创建时间
     */
    @ApiModelProperty("create_time")
    @Column(name = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 附件列表
     */
    @ApiModelProperty("附件列表")
    private List<AttachmentVo> attachmentVos;

    /**
     * 附件列表
     */
    @ApiModelProperty("二级任务协同列表")
    private List<JudgeTaskCoordinationVo> judgeTaskCoordinationTwos;
}