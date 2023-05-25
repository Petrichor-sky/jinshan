package com.haizhi.empower.config;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.bdp.joif.base.threadlocal.ThreadLocalToken;
import cn.bdp.joif.base.threadlocal.ThreadLocalUserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.haizhi.empower.util.IPUtils;


/**
 * 打印日志
 *
 * @Author chenb
 * @Description LogInterceptor
 * @Date 2019年12月24日13:37:55
 **/
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String userCode = ThreadLocalUserId.get();
        String accessToken = ThreadLocalToken.get();
        String uri = request.getRequestURI();
        String ipAddress = IPUtils.getIpAddress(request);
        log.info("LogInterceptor===>用户({})请求接口({}),携带的token({}),请求来源ip({})", userCode, uri, accessToken, ipAddress);
        return true;
    }

}

