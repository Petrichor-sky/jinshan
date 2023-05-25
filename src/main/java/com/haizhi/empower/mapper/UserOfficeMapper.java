package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.UserOffice;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 用户组织表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-12 17:38:45
 */
public interface UserOfficeMapper extends Mapper<UserOffice> {

    List<UserOffice> selectInfoByCreator(@Param("handlerCode") String handlerCode);

    UserOffice selectByUserCode(@Param("userCode") String userCode);
}
