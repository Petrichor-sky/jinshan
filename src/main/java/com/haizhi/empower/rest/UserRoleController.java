package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.entity.po.UserRole;
import com.haizhi.empower.service.UserRoleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("userRole")
public class UserRoleController extends BaseController<UserRoleService, UserRole> {

}