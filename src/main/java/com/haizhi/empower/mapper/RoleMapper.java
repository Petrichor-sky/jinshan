package com.haizhi.empower.mapper;

import java.util.List;

import com.haizhi.empower.entity.po.Role;
import com.haizhi.empower.entity.vo.RoleVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 角色信息
 * 
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:33
 */
public interface RoleMapper extends Mapper<Role> {

    List<RoleVo> selectRoleVosByUserCode(@Param("userCode") String userCode);

    List<Role> getAllRoles(@Param("keyword") String keyword);
}
