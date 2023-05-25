package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.DicItem;
import com.haizhi.empower.entity.vo.DicItemVo;
import com.haizhi.empower.entity.vo.SelectDictVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 字典子表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
public interface DicItemMapper extends Mapper<DicItem> {

    List<DicItemVo> getDicItemList(@Param("parentCode") String parentCode,
                                   @Param("keyword") String keyword);

    List<SelectDictVo> getDictItemByCode(@Param("keyword") String keyword);
}
