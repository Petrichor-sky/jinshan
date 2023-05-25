package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.OfficeInfoBo;
import com.haizhi.empower.entity.po.OfficeInfo;
import com.haizhi.empower.service.OfficeInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("officeInfo")
@Api(tags = "组织机构")
public class OfficeInfoController extends BaseController<OfficeInfoService, OfficeInfo> {

    /**
     * 获取当前单位及下辖单位树
     *
     * @param officeCode
     * @return
     */
    @GetMapping("permissionOfficeTrees")
    @ApiOperation(value ="获取当前单位及下辖单位树",notes = "获取当前单位及下辖单位树")
    public BaseResponse getPermissionOffices(@RequestParam String officeCode) {
        return baseService.getPermissionOffices(officeCode);
    }

    /**
     * 全量同步组织信息
     */
    @GetMapping("sync")
    @ApiOperation(value ="全量同步组织信息",notes = "全量同步组织信息")
    public BaseResponse syncAllUsers() {
        baseService.syncAllGroups();
        return new ObjectRestResponse<>();
    }

    /**
     * 获取组织树
     *
     * @param keyword
     * @return
     */
    @GetMapping("officeTree")
    @ApiOperation(value ="获取组织树",notes = "获取组织树")
    public BaseResponse getOfficeTree(@RequestParam(required = false, name = "keyword", defaultValue = "") String keyword) {
        return baseService.getOfficeTree(keyword);
    }

    /**
     * 获取组织用户树
     *
     * @param keyword
     * @return
     */
    @GetMapping("officeUsers")
    @ApiOperation(value ="获取组织用户树",notes = "获取组织用户树")
    public BaseResponse getOfficeUserTree(@RequestParam(required = false, name = "keyword", defaultValue = "") String keyword) {
        return baseService.getOfficeUserTree(keyword);
    }

    /**
     * 新增/修改
     *
     * @param officeInfoBo
     * @return
     */
    @PostMapping("modify")
    @ApiOperation(value ="新增/修改",notes = "新增/修改")
    public BaseResponse addOfficeInfo(@RequestBody @Valid OfficeInfoBo officeInfoBo) {
        return baseService.addOfficeInfo(officeInfoBo);
    }

    /**
     * 组织单位详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail")
    @ApiOperation(value ="组织单位详情",notes = "组织单位详情")
    public BaseResponse officeDetail(@RequestParam Long id) {
        return baseService.officeDetail(id);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("delete")
    @ApiOperation(value ="删除",notes = "删除")
    public BaseResponse deleteOffice(@RequestParam Long id) {
        return baseService.deleteOffice(id);
    }

    /**
     * 组织下用户列表
     *
     * @param officeCode
     * @param userName
     * @param name
     * @param copCode
     * @param roleId
     * @param status
     * @return
     */
    @GetMapping("users")
    @ApiOperation(value ="组织下用户列表",notes= "组织下用户列表")
    public BaseResponse getOfficeUsers(@RequestParam String officeCode,
                                       @RequestParam(required = false, name = "userName", defaultValue = "") String userName,
                                       @RequestParam(required = false, name = "name", defaultValue = "") String name,
                                       @RequestParam(required = false, name = "copCode", defaultValue = "") String copCode,
                                       @RequestParam(required = false, name = "roleId", defaultValue = "-1") Integer roleId,
                                       @RequestParam(required = false, name = "status", defaultValue = "-1") Integer status,
                                       @RequestParam(required = false, name = "keyword", defaultValue = "") String keyword,
                                       @RequestParam(required = false, name = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(required = false, name = "pageSize", defaultValue = "100") Integer pageSize) {
        return baseService.getOfficeUsers(officeCode, userName, name, copCode, roleId, status, keyword, pageNum, pageSize);
    }

    /**
     * 批量删除组织下用户
     *
     * @param batchUserBo
     * @return
     */
    @PostMapping("deleteUsers")
    @ApiOperation(value ="批量删除组织下用户",notes="批量删除组织下用户")
    public BaseResponse deleteUsers(@RequestBody @Valid OfficeInfoBo.BatchUserBo batchUserBo) {
        return baseService.deleteUsers(batchUserBo);
    }

    /**
     * 批量添加组织下用户
     *
     * @param batchUserBo
     * @return
     */
    @PostMapping("addUsers")
    @ApiOperation(value ="批量添加组织下用户",notes="批量添加组织下用户")
    public BaseResponse addUsers(@RequestBody @Valid OfficeInfoBo.BatchUserBo batchUserBo) {
        return baseService.addUsers(batchUserBo);
    }
}