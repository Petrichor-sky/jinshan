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
@ApiModel("研判任务事件")
public class JudgeTaskEventBo {
    /**
     * 事件id
     */
    @ApiModelProperty("事件id")
    @Column(name="event_id")
    private List<String> eventId;

    /**
     * 事件类型，字典表judge_event_type
     */
    @ApiModelProperty("事件类型，字典表judge_event_type")
    @Column(name="event_type")
    private String eventType;
}
