package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.UserRole;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 用户角色表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
public interface UserRoleMapper extends Mapper<UserRole> {

    /**
     * 通过userCode查询用户角色身份id*/
    Integer selectByUserCode(@Param("userCode") String userCode);

}
