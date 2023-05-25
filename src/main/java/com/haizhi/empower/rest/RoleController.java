package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.entity.bo.RoleBo;
import com.haizhi.empower.entity.po.Role;
import com.haizhi.empower.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("role")
@Api(tags = "角色")
public class RoleController extends BaseController<RoleService, Role> {

    /**
     * 获取用户中心所有角色
     *
     * @return
     */
    @GetMapping("allUcRoles")
    @ApiOperation(value ="获取所有UC角色",notes = "所有UC角色")
    public BaseResponse getAllUcRoles(){
        return baseService.getAllUcRoles();
    }

    /**
     * 角色详情，包括角色所拥有的的菜单
     *
     * @param id
     * @return
     */
    @GetMapping("detail")
    @ApiOperation(value = "角色详情",notes="角色详情")
    public BaseResponse getDetail(@RequestParam String id) {
        return baseService.getDetail(id);
    }

    /**
     * 角色菜单权限修改
     *
     * @param roleBo
     * @return
     */
    @PostMapping("modify")
    @ApiOperation(value ="角色权限修改",notes = "角色权限修改")
    public BaseResponse modify(@RequestBody @Valid RoleBo roleBo) {
        return baseService.modify(roleBo);
    }

    /**
     * 给系统管理员角色赋予所有菜单权限
     *
     * @return
     */
    @GetMapping("adminPermission")
    @ApiOperation(value ="系统管理员赋权",notes = "系统管理员赋权")
    public BaseResponse adminPermission(@RequestParam String roleId) {
        return baseService.adminPermission(roleId);
    }

}