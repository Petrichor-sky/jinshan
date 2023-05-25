package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.PermissionBo;
import com.haizhi.empower.entity.po.Permission;
import com.haizhi.empower.entity.vo.PermissionVo;
import com.haizhi.empower.entity.vo.RoleVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.enums.PermissionResTypeEnum;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.mapper.PermissionMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单(权限)信息表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-28 15:48:25
 */
@Service
public class PermissionService extends BaseService<PermissionMapper, Permission> {

    @Autowired
    AttachmentService attachmentService;

    public BaseResponse getList(Integer status, String keyword) {
        List<Permission> permissions = mapper.getList(status, keyword);

        if (!StringUtils.isEmpty(keyword)) {
            List<Permission> temp = new ArrayList<>();
            permissions.stream().filter(Objects::nonNull).filter(data -> data.getLevel() != 1).forEach(data -> {
                getParentPermission(temp, data);
            });
            permissions.addAll(temp);
        }

        List<PermissionVo.PermissionTreeVo> vos = permissions.stream().distinct().filter(data -> data.getLevel() == 1).map(data -> {
            return PermissionVo.PermissionTreeVo.builder()
                    .name(data.getName())
                    .builtIn(data.getBuiltIn())
                    .code(data.getCode())
                    .id(data.getId())
                    .itemOrder(data.getItemOrder())
                    .level(data.getLevel())
                    .linkType(data.getLinkType())
                    .resDesc(data.getResDesc())
                    .resRoute(data.getResRoute())
                    .resType(data.getResType())
                    .routeType(data.getRouteType())
                    .status(data.getStatus())
                    .superiorCode(data.getSuperiorCode())
                    .iconUrl(attachmentService.getAttachmentById(data.getIcon().longValue()))
                    .subs(data.getResType() == 1 ? new ArrayList<>() :
                            generateChildSubs(data.getCode(), data.getLevel() + 1, permissions)
                                    .stream().sorted((o1, o2) -> {
                                return o2.getItemOrder() - o1.getItemOrder();
                            }).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        return new ObjectRestResponse<>().data(vos);
    }


    public List<PermissionVo.PermissionTreeVo> getPermissionTreeVosByRoleVos(List<RoleVo> roleVos, String sysCode) {

        List<Permission> permissions = mapper.getRolesPermissions(roleVos.stream().map(RoleVo::getRoleId).collect(Collectors.toList()), sysCode);

        List<Permission> temp = new ArrayList<>();
        permissions.stream().filter(Objects::nonNull).filter(data -> data.getLevel() != 1).forEach(data -> {
            getParentPermission(temp, data);
        });
        permissions.addAll(temp);

        List<PermissionVo.PermissionTreeVo> vos = permissions.stream().distinct().
                filter(data -> data.getLevel() == 1).map(data -> {
            return PermissionVo.PermissionTreeVo.builder()
                    .name(data.getName())
                    .builtIn(data.getBuiltIn())
                    .code(data.getCode())
                    .id(data.getId())
                    .itemOrder(data.getItemOrder())
                    .level(data.getLevel())
                    .linkType(data.getLinkType())
                    .resDesc(data.getResDesc())
                    .resRoute(data.getResRoute())
                    .resType(data.getResType())
                    .routeType(data.getRouteType())
                    .status(data.getStatus())
                    .superiorCode(data.getSuperiorCode())
                    .iconUrl(attachmentService.getAttachmentById(data.getIcon().longValue()))
                    .subs(data.getResType() == 1 ? new ArrayList<>() :
                            generateChildSubs(data.getCode(), data.getLevel() + 1,
                                    permissions.stream()
                                            .distinct()
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()))
                                    .stream().sorted((o1, o2) -> {
                                return o2.getItemOrder() - o1.getItemOrder();
                            }).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        return vos;
    }

    private List<Permission> getParentPermission(List<Permission> permissions, Permission data) {
        Permission permission = selectOne(Permission.builder().level(data.getLevel() - 1).dr(DeleteStatusEnum.DEL_NO.getCode())
                .code(data.getSuperiorCode()).build());

        if (permission != null) {
            if (!permissions.contains(permission)) {
                permissions.add(permission);
                getParentPermission(permissions, permission);
            } else {
                return permissions;
            }

        }
        return permissions;
    }

    private List<PermissionVo.PermissionTreeVo> generateChildSubs(String code, int level, List<Permission> permissions) {
        return permissions.stream()
                .filter(data -> data.getLevel() == level)
                .filter(data -> data.getSuperiorCode().equals(code))
                .map(data -> {
                    return PermissionVo.PermissionTreeVo.builder()
                            .name(data.getName())
                            .builtIn(data.getBuiltIn())
                            .code(data.getCode())
                            .id(data.getId())
                            .itemOrder(data.getItemOrder())
                            .level(data.getLevel())
                            .linkType(data.getLinkType())
                            .resDesc(data.getResDesc())
                            .resRoute(data.getResRoute())
                            .resType(data.getResType())
                            .routeType(data.getRouteType())
                            .status(data.getStatus())
                            .superiorCode(data.getSuperiorCode())
                            .iconUrl(attachmentService.getAttachmentById(data.getIcon().longValue()))
                            .subs(data.getResType() == 1 ? new ArrayList<>() :
                                    generateChildSubs(data.getCode(), data.getLevel() + 1, permissions)
                                            .stream().sorted((o1, o2) -> {
                                        return o2.getItemOrder() - o1.getItemOrder();
                                    }).collect(Collectors.toList()))
                            .build();
                }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class, value = "mysqlTransactionManager")
    public BaseResponse modified(PermissionBo.ModifiedBo modifiedBo) {
        boolean isAdd = ObjectUtils.isEmpty(modifiedBo.getId());

        Permission query = Permission.builder()
                .name(modifiedBo.getName())
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build();
        if (!ObjectUtils.isEmpty(modifiedBo.getSuperiorCode())) {
            query.setSuperiorCode(modifiedBo.getSuperiorCode());
            Permission permission1 = selectOne(Permission.builder().dr(DeleteStatusEnum.DEL_NO.getCode()).code(modifiedBo.getSuperiorCode())
                    .build());
            if (permission1 != null) {
                query.setLevel(permission1.getLevel() + 1);
            }
        } else {
            query.setLevel(1);
        }

        List<Permission> result = selectList(query);
        if (isAdd) {
            if (result.size() > 0) {
                throw new RuntimeException("同级目录下已有同名菜单或目录");
            }
        } else {
            if (result.size() > 1) {
                throw new RuntimeException("同级目录下已有同名菜单或目录");
            } else if (result.size() == 1) {
                if (!modifiedBo.getId().equals(result.get(0).getId())) {
                    throw new RuntimeException("同级目录下已有同名菜单或目录");
                }
            }
        }

        // 新增菜单放到最后
        Integer itemOrder = 1;
        Integer lastItemOrder = mapper.queryLastItemOrder(modifiedBo.getSuperiorCode());
        if (!ObjectUtils.isEmpty(lastItemOrder)) {
            itemOrder = lastItemOrder + 1;
        }

        Permission permission;
        if (isAdd) {
            permission = Permission.builder()
                    .name(modifiedBo.getName())
                    .linkType(0)
                    .superiorCode(modifiedBo.getSuperiorCode())
                    .code(genPermissionCode(modifiedBo.getResType()))
                    .createTime(new Date())
                    .itemOrder(itemOrder)
                    .builtIn(1)
                    .createTime(new Date())
                    .build();
            if (!ObjectUtils.isEmpty(modifiedBo.getSuperiorCode()) && !StringUtils.isEmpty(modifiedBo.getSuperiorCode())) {
                permission.setSuperiorCode(modifiedBo.getSuperiorCode());
                permission.setCode(genPermissionCode(modifiedBo.getResType()));
                Permission permission1 = selectOne(Permission.builder().dr(DeleteStatusEnum.DEL_NO.getCode()).code(modifiedBo.getSuperiorCode())
                        .build());
                if (permission1 != null) {
                    permission.setLevel(permission1.getLevel() + 1);
                } else {
                    permission.setLevel(1);
                }
            } else {
                permission.setLevel(1);
            }
            permission.setStatus(1);
            permission.setDr(DeleteStatusEnum.DEL_NO.getCode());
            permission.setResType(modifiedBo.getResType());

        } else {
            permission = selectById(modifiedBo.getId());
            if(StringUtils.isNotBlank(permission.getSuperiorCode())) {
                if (!permission.getSuperiorCode().equals(modifiedBo.getSuperiorCode())) {
                    throw new RestException("不允许修改父级目录");
                }
            }
                if (permission.getBuiltIn() == 0) {
                    throw new RestException("系统目录不允许修改");
                }

        }

        if (modifiedBo.getResType() == 1) {
            permission.setResRoute(modifiedBo.getResRoute());
            permission.setRouteType(modifiedBo.getRouteType());
        }
        permission.setResDesc(modifiedBo.getResDesc());
        permission.setUpdateTime(new Date());
        permission.setName(modifiedBo.getName());
        if (ObjectUtils.isEmpty(modifiedBo.getIcon())) {
            permission.setIcon(1);
        } else {
            permission.setIcon(modifiedBo.getIcon());
        }

        if (isAdd) {
            insert(permission);
        } else {
            updateById(permission);
        }

        return new BaseResponse();
    }

    /**
     * 生成code
     *
     * @param resType
     * @return
     */
    private String genPermissionCode(Integer resType) {
        String pre = PermissionResTypeEnum.getPre(resType);
        return pre + System.currentTimeMillis();
    }

    public BaseResponse deletePermission(Long id) {
        Permission permission = selectOne(Permission.builder().id(id).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (permission == null) {
            throw new RuntimeException("id为：" + id + "的菜单或目录不存在");
        }

        if (permission.getBuiltIn() == 0) {
            throw new RuntimeException("内置菜单或目录不允许删除");
        }

        permission.setDr(DeleteStatusEnum.DEL_YES.getCode());
        updateById(permission);
        rolePermissionService.deletePermission(id);
        return new BaseResponse();
    }

}