package com.haizhi.empower.mapper;

import java.util.List;

import com.haizhi.empower.entity.po.DicType;
import com.haizhi.empower.entity.vo.DicTypeVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 字典表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
public interface DicTypeMapper extends Mapper<DicType> {

    List<DicTypeVo> getDicTypes(@Param("keyword") String keyword);
}
