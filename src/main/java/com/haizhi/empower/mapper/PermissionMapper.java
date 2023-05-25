package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.Permission;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 菜单(权限)信息表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-28 15:48:25
 */
public interface PermissionMapper extends Mapper<Permission> {

    List<Permission> getList(@Param("status") Integer status,
                             @Param("keyword") String keyword);

    Integer queryLastItemOrder(@Param("superiorCode") String superiorCode);

    List<Permission> getRolesPermissions(@Param("roleIds") List<String> roleIds, @Param("sysCode") String sysCode);
}
