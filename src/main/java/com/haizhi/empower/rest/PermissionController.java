package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.PermissionBo;
import com.haizhi.empower.entity.po.Permission;
import com.haizhi.empower.entity.vo.RoleVo;
import com.haizhi.empower.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("permission")
@Api(tags = "权限")
public class PermissionController extends BaseController<PermissionService, Permission> {

    /**
     * 获取权限列表
     *
     * @param status
     * @param keyword
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value ="获取所有菜单权限",notes = "获取所有菜单权限")
    public BaseResponse getList(@RequestParam(required = false, name = "status", defaultValue = "-1") Integer status,
                                @RequestParam(required = false, name = "keyword", defaultValue = "") String keyword) {
        return baseService.getList(status, keyword);
    }

    /**
     * 新增/修改
     *
     * @param modifiedBo
     * @return
     */
    @PostMapping("modified")
    @ApiOperation(value ="新增/修改",notes = "新增/修改")
    public BaseResponse modified(@RequestBody @Valid PermissionBo.ModifiedBo modifiedBo) {
        return baseService.modified(modifiedBo);
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    @GetMapping("delete")
    @ApiOperation(value ="删除菜单",notes="删除菜单")
    public BaseResponse deletePermission(@RequestParam Long id) {
        return baseService.deletePermission(id);
    }


    /**
     * 根据角色Id查询菜单权限
     *
     * @param roleId
     * @param sysCode
     * @return
     */
    @GetMapping("rolePermission")
    public BaseResponse getPermission(@RequestParam("roleId") String roleId,
                                      @RequestParam(name = "sysCode",required = false,defaultValue = "") String sysCode) {
        List<RoleVo> roleVos = new ArrayList();
        roleVos.add(RoleVo.builder()
                .roleId(roleId)
                .build());
        return new ObjectRestResponse<>().data(baseService.getPermissionTreeVosByRoleVos(roleVos, sysCode));
    }
}