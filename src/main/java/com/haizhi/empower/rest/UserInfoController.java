package com.haizhi.empower.rest;

import cn.bdp.joif.service.JoifUcService;
import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.UserInfoBo;
import com.haizhi.empower.entity.po.UserInfo;
import com.haizhi.empower.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@CrossOrigin
@RestController
@RequestMapping("/userInfo/")
@Api(tags = "用户")
public class UserInfoController extends BaseController<UserInfoService, UserInfo> {
    @Autowired
    JoifUcService joifUcService;
//    /**
//     * 登录
//     *
//     * @param loginBo
//     * @param response
//     * @return
//     */
//    @PostMapping("login")
//    public BaseResponse login(@RequestBody @Valid UserInfoBo.UserLoginBo loginBo,
//                              HttpServletResponse response) {
//        return baseService.login(loginBo, response);
//    }
//
//
//
    /**
     * 单点登录
     *
     * @param response
     * @return
     */
    @PostMapping("ssoLogin")
    public BaseResponse ssoLogin(HttpServletResponse response) {
        return baseService.ssoLogin(response);
    }

    @GetMapping("logout")
    public BaseResponse logout(HttpServletResponse response) {
        return baseService.logout(response);
    }

    /**
     * 当前用户基本信息
     *
     * @return
     */
    @GetMapping("currentUser")
    public BaseResponse getCurrentUserInfo() {
        return new ObjectRestResponse<>().data(baseService.getCurrentUser());
    }

    /**
     * 修改密码
     *
     * @param userCode
     * @param oldPass
     * @param newPass
     * @return
     */
    @GetMapping("changePassword")
    public BaseResponse changePassword(@RequestParam String userCode,
                                       @RequestParam String oldPass,
                                       @RequestParam String newPass) {
        return baseService.changePassword(userCode, oldPass, newPass);
    }

    /**
     * 设置默认登录组织
     *
     * @return
     */
    @PostMapping("defaultOffice")
    public BaseResponse setDefaultOffice(@RequestBody @Valid UserInfoBo.ChangePasswordBo changePasswordBo) {
        baseService.setDefaultOffice(changePasswordBo.getUserCode(), changePasswordBo.getOfficeCode(), changePasswordBo.getDefaultOffice());
        return new BaseResponse();
    }


//    /**
//     * 添加用户/修改
//     *
//     * @param addBo
//     * @return
//     */
//    @PostMapping("modify")
//    public BaseResponse add(@RequestBody @Valid UserInfoBo.AddBo addBo) {
//        return baseService.addUserInfo(addBo);
//    }
//
//    /**
//     * 用户详情回显
//     *
//     * @param userCode
//     * @return
//     */
//    @GetMapping("detail")
//    public BaseResponse detail(@RequestParam String userCode) {
//        return baseService.detail(userCode);
//    }
//
//    /**
//     * 停启用用户
//     *
//     * @param userCode
//     * @return
//     */
//    @GetMapping("changeStatus")
//    public BaseResponse changeStatus(@RequestParam String userCode,
//                                     @RequestParam Integer status) {
//        return baseService.changeStatus(userCode, status);
//    }
//
//
//    /**
//     * 重置密码
//     *
//     * @param userCode
//     * @return
//     */
//    @GetMapping("resetPassword")
//    public BaseResponse resetPassword(@RequestParam String userCode) {
//        return baseService.resetPassword(userCode);
//    }
//
//    /**
//     * 删除用户
//     *
//     * @param userCode
//     * @return
//     */
//    @GetMapping("delete")
//    public BaseResponse deleteUser(@RequestParam String userCode) {
//        return baseService.deleteUser(userCode);
//    }
//
//    /**
//     * 根据userCode获取用户信息*/
//    @GetMapping("byUserCode")
//    public BaseResponse queryUserInfo(String userCode) {
//        return new ObjectRestResponse<>().data(baseService.getUserInfo(userCode));
//    }
//
//    @ApiOperation(value = "同步所有用户信息")
//    @GetMapping("syncAllUser")
//    public BaseResponse syncAllUser() {
//        return baseService.syncAllUser();
//    }

    @ApiOperation(value = "获取组织用户树形结构")
    @GetMapping("getOfficeUserTree")
    public BaseResponse getOfficeUserTree(){
        return baseService.getOfficeUserTree();
    }

    @ApiOperation(value = "获取组所有组织")
    @GetMapping("getAllOffice")
    public BaseResponse getAllOffice(){
        return new ObjectRestResponse<>().data(joifUcService.getAllGroups());
    }
}