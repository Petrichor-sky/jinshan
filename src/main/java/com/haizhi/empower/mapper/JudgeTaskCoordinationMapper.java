package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.JudgeTaskCoordination;
import com.haizhi.empower.entity.vo.JudgeTaskCoordinationVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

public interface JudgeTaskCoordinationMapper extends Mapper<JudgeTaskCoordination> {

    /**
     * 根据父级主键查询列表
     * @param parentId 父级主键
     * @param taskId 任务主键
     * @return
     */
    List<JudgeTaskCoordinationVo> selectByParentId(@Param("parentId") int parentId, @Param("taskId") int taskId);

    /**
     * 获取任务的所有二级协同
     * @param oneLevelCoordinationIdList
     * @return
     */
    List<JudgeTaskCoordinationVo> selectTwoLevelCoordinationByIds(@Param("parentIds") List<Integer> oneLevelCoordinationIdList, @Param("taskId") Integer taskId);
}
