package com.haizhi.empower.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.RoleBo;
import com.haizhi.empower.entity.dto.UcApiResult;
import com.haizhi.empower.entity.dto.UcRoleDetailDto;
import com.haizhi.empower.entity.po.Permission;
import com.haizhi.empower.entity.po.Role;
import com.haizhi.empower.entity.po.RolePermission;
import com.haizhi.empower.entity.vo.RoleVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.exception.ServiceException;
import com.haizhi.empower.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.bdp.joif.conststant.ConstantPool.UcConsts.UC_SUC_CODE;
import static com.haizhi.empower.base.AppConstants.Uc.ROLE_INFO;
import static com.haizhi.empower.base.AppConstants.Uc.ROLE_LIST;


/**
 * 角色信息
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:33
 */
@Service
@Slf4j
public class RoleService extends BaseService<RoleMapper, Role> {

    @Autowired
    RolePermissionService rolePermissionService;

    @Autowired
    PermissionService permissionService;


    /**
     * 获取用户中心所有角色
     *
     * @return
     */
    public BaseResponse getAllUcRoles() {
        UcApiResult ucApiResult = callUc(ROLE_LIST, null,false)
                .orElseThrow(() -> new RuntimeException("查询角色列表异常！"));
        if (!UC_SUC_CODE.equals(ucApiResult.getStatus())) {
            throw new RuntimeException("查询角色列表异常-" + ucApiResult.getErrstr());
        }
        return new ObjectRestResponse<>()
                .data(ucApiResult.getResult())
                .traceId(ucApiResult.getTrace_id());
    }

    /**
     * 根据用户角色查询角色详情和菜单
     *
     * @param roleId
     * @return
     */
    public BaseResponse getDetail(String roleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("role_id", roleId);
        UcApiResult ucApiResult = callUc(ROLE_INFO, params,false)
                .orElseThrow(() -> new RuntimeException("查询角色信息异常！"));
        if (!UC_SUC_CODE.equals(ucApiResult.getStatus())) {
            throw new RuntimeException("查询角色信息异常-" + ucApiResult.getErrstr());
        }
        UcRoleDetailDto ucRoleDetailDto = JSON.parseObject(ucApiResult.getResult().toString(), UcRoleDetailDto.class);

        RoleVo.RoleDetailVo vo = RoleVo.RoleDetailVo.builder()
                .roleId(ucRoleDetailDto.getRole_id())
                .roleName(ucRoleDetailDto.getRole_name())
                .desc(ucRoleDetailDto.getRole_desc())
                .type(ucRoleDetailDto.getRole_type())
                .rolePermissionVos(rolePermissionService.selectList(RolePermission.builder()
                        .roleId(roleId)
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .build()).stream().filter(Objects::nonNull).map(data -> {
                    return permissionService.selectOne(Permission.builder()
                            .dr(DeleteStatusEnum.DEL_NO.getCode())
                            .id(data.getPermissionId())
                            .build());
                }).filter(Objects::nonNull).map(data -> {
                    return RoleVo.RolePermissionVo.builder()
                            .permissionId(data.getId().intValue())
                            .permissionName(data.getName())
                            .build();
                }).collect(Collectors.toList()))
                .build();
        return new ObjectRestResponse<>().data(vo);
    }

    public List<RoleVo> selectRoleVosByUserCode(String userCode) {
        return mapper.selectRoleVosByUserCode(userCode).stream().distinct().collect(Collectors.toList());
    }


    /**
     * 修改UC角色对应的菜单权限
     *
     * @param roleBo
     * @return
     */
    public BaseResponse modify(@Valid RoleBo roleBo) {
        if (StringUtils.isEmpty(roleBo.getId())){
            throw new ServiceException("角色Id不能为空！");
        }
        rolePermissionService.delete(RolePermission.builder()
                .roleId(roleBo.getId())
                .build());
        roleBo.getPermissions().stream().forEach(data -> {
            rolePermissionService.insert(RolePermission.builder()
                    .roleId(roleBo.getId())
                    .permissionId(data)
                    .authorizationType(1)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build());
        });
        return new BaseResponse();
    }

    /**
     * 给系统管理员角色赋予所有菜单权限
     *
     * @param roleId
     * @return
     */
    public BaseResponse adminPermission(String roleId) {
        List<Permission> permissionList = permissionService.selectList(Permission.builder().dr(0).build());
        List<Long> permissions = permissionList.stream().map(Permission::getId).collect(Collectors.toList());
        return modify(RoleBo.builder().id(roleId).permissions(permissions).build());
    }
}