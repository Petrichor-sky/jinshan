package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.entity.po.UserOffice;
import com.haizhi.empower.mapper.UserOfficeMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * 用户组织表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-04-12 17:38:45
 */
@Service
public class UserOfficeService extends BaseService<UserOfficeMapper, UserOffice> {

    public String getUserOffice(String userCode){
        Example e=new Example(UserOffice.class);
        e.createCriteria().andEqualTo("userCode",userCode);
        e.createCriteria().andEqualTo("isDefault",1);
        e.createCriteria().andEqualTo("dr",0);
        String userOfficeCode = this.selectByExample(e).get(0).getOfficeCode();
        return userOfficeCode;
    }
}