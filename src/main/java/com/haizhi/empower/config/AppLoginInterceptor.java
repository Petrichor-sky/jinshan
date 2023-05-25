package com.haizhi.empower.config;

import com.haizhi.empower.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author by fuhanchao
 * @date 2022/12/6.
 */
@Slf4j
@Component
public class AppLoginInterceptor implements HandlerInterceptor {

    @Value("${tokenName}")
     String tokenName;//="app:auth:"
    @Autowired
    RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        String accessToken=request.getHeader("access_token");
//
//        String token=tokenName+":"+accessToken;
//        String method=request.getHeader("method");
//        if("login".equals(method)) {
//            log.info("AppLoginInterceptor===>用户登录");
//
//            return true;
//        }else {
//            if (redisUtils.exists(token)) {
//
//                String uri = request.getRequestURI();
//                String ipAddress = IPUtils.getIpAddress(request);
//                log.info("AppLoginInterceptor===>用户请求接口({}),携带的token({}),请求来源ip({})", uri, accessToken, ipAddress);
//                return true;
//            } else {
//                JSONObject result=new JSONObject();
//                result.put("status","401");
//                result.put("msg","未登录");
//                returnJosn(response,JSONObject.toJSONString(result));
//                log.error("AppLoginInterceptor===>token已过期");
//                return false;
//            }
//        }
        return true;
    }

    public void returnJosn(HttpServletResponse response, String result){
        PrintWriter writer=null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            writer=response.getWriter();
            writer.write(result);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
