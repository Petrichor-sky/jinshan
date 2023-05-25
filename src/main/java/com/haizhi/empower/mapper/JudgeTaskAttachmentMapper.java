package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.JudgeTaskAttachment;
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
public interface JudgeTaskAttachmentMapper extends Mapper<JudgeTaskAttachment> {

    int insertList(@Param("judgeTaskAttachments") List<JudgeTaskAttachment> judgeTaskAttachments);

    /**
     * 通过id 表名称查询对应附件
     * @param tableName 表名称
     * @param tableId id
     * @return
     */
    List<JudgeTaskAttachment> selectByTableName(@Param("tableName") String tableName, @Param("tableId") int tableId);

    /**
     * 查询附件id列表
     * @return
     */
    List<Long> selectAttachmentIds(@Param("tableName") String tableName, @Param("tableId") int tableId);

    /**
     * 通过id 表名称删除对应附件
     * @param tableName 表名称
     * @param tableId id
     * @return
     */
    int deleteByTableName(@Param("tableName") String tableName, @Param("tableId") int tableId);
}
