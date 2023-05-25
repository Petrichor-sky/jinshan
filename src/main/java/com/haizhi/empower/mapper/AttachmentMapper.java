package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.Attachment;
import com.haizhi.empower.entity.vo.AttachmentVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 附件管理表
 * 
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
public interface AttachmentMapper extends Mapper<Attachment> {
    /**
     * 根据附件id列表查询
     * @param ids id列表
     * @return
     */
    List<AttachmentVo> selectByIds(@Param("ids") List<Long> ids);
}
