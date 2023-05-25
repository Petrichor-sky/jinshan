package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.JudgeTaskEvent;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 研判任务协作者表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
public interface JudgeTaskEventMapper extends Mapper<JudgeTaskEvent> {

    int insertList(@Param("judgeTaskEvents") List<JudgeTaskEvent> judgeTaskEvents);

    /**
     * 查询
     * @return
     */
    List<JudgeTaskEvent> selectByTaskId(@Param("taskId") int taskId);

    /**
     * 删除
     * @return
     */
    int deleteByTaskId(@Param("taskId") int taskId);

    /**
     * 任务所有关联要素(事件)
     * @param taskId
     * @return
     */
    List<JudgeTaskEvent> selectEventByTaskId(@Param("taskId") Integer taskId);
}
