package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.dto.JudgeTaskDetailDto;
import com.haizhi.empower.entity.dto.JudgeTaskManagePageDto;
import com.haizhi.empower.entity.po.JudgeMessage;
import com.haizhi.empower.entity.po.JudgeTask;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * 研判任务表
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
public interface JudgeTaskMapper extends Mapper<JudgeTask> {

    List<JudgeTask> selectByTaskNo(@Param("taskNo") String taskNo);
    /**
     * 获取登录人创建的待办任务
     * @param userCode
     * @return
     */
    List<JudgeTask> selectCreatePendList(@Param("userCode") String userCode, @Param("taskStatus") String taskStatus);

    /**
     * 获取登录人协作的待办任务
     * @param collaboratorPendIdList
     * @return
     */
    List<JudgeTask> selectCollaboratorPendList(@Param("ids") List<Integer> collaboratorPendIdList, @Param("taskStatus") String taskStatus);

    /**
     * 获取登录人反馈消息(我创建的任务,别人反馈的)
     * @param userCode
     * @return
     */
    List<JudgeMessage> selectFeedbackMessageList(@Param("userCode") String userCode);

    /**
     * 获取登录人协作消息(别人创建的任务,需要我协作)
     * @param userCode
     * @return
     */
    List<JudgeMessage> selectCooperateMessageList(@Param("userCode") String userCode);

    /**
     * 获取当前登录人进行中的任务数
     * @param userCode
     * @return
     */
    List<Integer> selectProgressTaskCount(@Param("userCode") String userCode);

    /**
     * 获取当前登录人已完成的任务数
     * @param userCode
     * @return
     */
    List<Integer> selectFinishTaskCount(@Param("userCode") String userCode);

    /**
     * 获取当前登录人创建的任务数
     * @param userCode
     * @return
     */
    List<Integer> selectMyCreateTaskCount(@Param("userCode") String userCode);

    /**
     * 获取当前登录人创建的任务
     * @param userCode
     * @return
     */
    List<JudgeTask> selectMyCreateTaskByCode(@Param("userCode") String userCode);

    /**
     * 获取研判任务详情:
     * 创建单位唯一标识,创建人唯一标识,创建时间,任务名称,任务描述,研判脑图,任务结论
     * @param taskId
     * @return
     */
    JudgeTaskDetailDto selectTaskBaseInfo(@Param("taskId") Integer taskId);
}
