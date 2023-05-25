package com.haizhi.empower.entity.po;

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
@Table(name="t_judge_task_collaborator")
@ApiModel("研判任务协作者")
public class JudgeTaskCollaborator implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    @Column(name="task_id")
    private Integer taskId;

    /**
     * 协作者
     */
    @ApiModelProperty("协作者")
    @Column(name="collaborator")
    private String collaborator;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @Column(name="create_user")
    private String createUser;

    /**
     * create_time
     */
    @ApiModelProperty("create_time")
    @Column(name="create_time")
    private Date createTime;

    //删除标志(1:删除0:未删除)
    @Column(name = "dr")
    private Integer dr;
}
