package com.haizhi.empower.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/3/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="t_judge_task_coordination")
@ApiModel("研判任务协同")
public class JudgeTaskCoordination implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
     * 创建人唯一标识
     */
    @ApiModelProperty("创建人唯一标识")
    @Column(name="create_user")
    private String createUser;

    /**
     * 创建人单位唯一标识
     */
    @ApiModelProperty("创建人单位唯一标识")
    @Column(name="create_unit")
    private String createUnit;

    /**
     * 回复人唯一标识
     */
    @ApiModelProperty("回复人唯一标识")
    @Column(name="reply_name")
    private String replyName;

    /**
     * 创建时间
     */
    @ApiModelProperty("create_time")
    @Column(name="create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //删除标志(1:删除0:未删除)
    @Column(name = "dr")
    private Integer dr;
}
