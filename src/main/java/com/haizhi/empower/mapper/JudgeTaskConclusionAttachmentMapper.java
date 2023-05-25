package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.JudgeTaskConclusionAttachment;
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
public interface JudgeTaskConclusionAttachmentMapper extends Mapper<JudgeTaskConclusionAttachment> {

    int insertList(@Param("judgeTaskConclusionAttachments") List<JudgeTaskConclusionAttachment> judgeTaskConclusionAttachments);

    /**
     * 查询附件id
     * @return
     */
    List<Long> selectAttachmentIds(@Param("taskId") int taskId);

    /**
     * 删除附件
     * @return
     */
    int deleteByTaskId(@Param("taskId") int taskId);
}
