package com.haizhi.empower.base;


import static com.haizhi.empower.base.AppConstants.Key.TOKEN_HEADER;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

/**
 * 持久层基于mybatis的业务代码框架
 *
 * @author CristianWindy
 * @date: 2020年02月12日20:45:27
 */

public class BaseController<Service extends BaseService<? extends Mapper<Entity>, Entity>, Entity> {

    @Autowired
    protected Service baseService;

    /**
     * 获取当前用户账号
     *
     * @return
     */
    public String getAccount() {
        String account = ThreadLocalContext.get(TOKEN_HEADER);
        if (StringUtils.isEmpty(account)) {
            throw new RuntimeException("未能获取登录账号信息");
        }
        return account;
    }
}
