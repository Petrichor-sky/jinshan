package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.JudgeTaskCollaborator;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 研判任务协作者表
 *
 */
public interface JudgeTaskCollaboratorMapper extends Mapper<JudgeTaskCollaborator> {

    int insertList(@Param("collaborators") List<JudgeTaskCollaborator> collaborators);

    /**
     * 查询
     * @return
     */
    List<JudgeTaskCollaborator> selectByTaskId(@Param("taskId") int taskId);

    /**
     * 删除
     * @return
     */
    int deleteByTaskId(@Param("taskId") int taskId);

    /**
     * 获取登录人协作的任务id
     * @param userCode
     * @return
     */
    List<Integer> selectCollaboratorPendIdList(@Param("userCode") String userCode);

    /**
     * 获取研判任务详情:
     * 任务协作者唯一标识
     * @param taskId
     * @return
     */
    List<String> selectTaskCollaboratorCodeList(@Param("taskId") Integer taskId);
}
