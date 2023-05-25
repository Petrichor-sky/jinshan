package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.mapper.RolePermissionMapper;
import org.springframework.stereotype.Service;

import com.haizhi.empower.entity.po.RolePermission;

/**
 * 角色权限信息
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-28 15:48:25
 */
@Service
public class RolePermissionService extends BaseService<RolePermissionMapper, RolePermission> {
    public void deletePermission(Long permissionId) {
        selectList(RolePermission.builder()
                .permissionId(permissionId)
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build()).stream().forEach(data -> {
            data.setDr(DeleteStatusEnum.DEL_YES.getCode());
            updateById(data);
        });
    }
}