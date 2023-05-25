package com.haizhi.empower.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * 研判中心-首页消息通知vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "JudgeTaskHomePageMessageVo", description = "研判中心-首页消息通知vo")
public class JudgeTaskHomePageMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    @ApiModelProperty("任务主键")
    @Column(name="task_id")
    private Integer taskId;
    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    @Column(name="message_content")
    private String messageContent;
    /**
     * 消息类型: 1为协同消息(别人创建的) 2为反馈消息(我创建的)
     */
    @ApiModelProperty("消息类型: 1为协同消息(别人创建的) 2为反馈消息(我创建的)")
    private String messageType;
}
