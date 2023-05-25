package com.haizhi.empower.mapper;

import java.util.List;

import com.haizhi.empower.entity.bo.CurrentUserShowBo;
import com.haizhi.empower.entity.po.OfficeInfo;
import com.haizhi.empower.entity.po.UserInfo;
import com.haizhi.empower.entity.vo.OfficeVo;
import com.haizhi.empower.entity.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 组织信息表
 * 
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
public interface OfficeInfoMapper extends Mapper<OfficeInfo> {

    OfficeVo selectOfficeByOfficeValue(@Param("officeType") Integer officeType,
                                       @Param("officeValue")String officeValue);

    List<OfficeInfo> selectOfficeFilterByName(@Param("keyword") String keyword);

    List<UserVo> getUsersByOfficeCode(@Param("officeCode") String officeCode, @Param("keyword")String keyword);

    List<OfficeInfo> selectUserOfficeFilterByName(@Param("keyword") String keyword);

    List<CurrentUserShowBo.OfficeVo> getOffices(@Param("userId") String userId);

    List<OfficeVo> selectOfficesByOfficeValue(@Param("officeType") Integer officeType,
                                              @Param("officeValue") String officeValue);
}
