package com.haizhi.empower.entity.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @description 分析研判消息
 * @author zhang jia hao
 * @date 2023-03-31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_judge_message")
public class JudgeMessage implements Serializable {
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
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    @Column(name="message_content")
    private String messageContent;

    /**
     * 消息类型，字典表judge_message_type
     */
    @ApiModelProperty("消息类型，字典表judge_message_type")
    @Column(name="message_type")
    private String messageType;

    /**
     * create_user
     */
    @ApiModelProperty("create_user")
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
