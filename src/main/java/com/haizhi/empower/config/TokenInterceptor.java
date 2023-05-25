/*
package com.haizhi.situation.config;


import com.alibaba.fastjson.JSON;
import com.haizhi.situation.base.AppConstants;
import com.haizhi.situation.base.ThreadLocalContext;
import com.haizhi.situation.base.ThreadLocalUserId;
import com.haizhi.situation.base.resp.ObjectRestResponse;
import com.haizhi.situation.service.UserInfoService;
import com.haizhi.situation.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.haizhi.situation.base.AppConstants.Key.*;


*
 * @author CristianWindy
 * @Description TokenHandler
 * @Date 30/6/2019 10:44 AM
 *

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private UserInfoService userInfoService;
    @Value("${webConfig.isOpen:true}")
    private Boolean isOpen;

    @Value("${info.sysUrl.loginPath}")
    String loginPath;

    public TokenInterceptor(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理请求跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setCharacterEncoding("utf-8");

        //获取请求携带的token
        Triple<String, String, String> tokensPair = getToken(request);
        String access_token = tokensPair.getLeft();
        String user_id = tokensPair.getMiddle();
        String userCode = tokensPair.getRight();
        String uri = request.getRequestURI();
        String ipAddress = IPUtils.getIpAddress(request);
        log.info("用户[{}]请求[{}]携带的token:{}", ipAddress, uri, access_token);
        ThreadLocalContext.set(URI, uri);
        //token存在的话需要获取token携带的用户信息
        if (!AppConstants.Key.WhiteUrls.contains(uri)) {
            if (isOpen) {
                if (StringUtils.isEmpty(userCode)) {
                    removeToken(response);
                    response.setContentType("application/json;charset=UTF-8");
                    ObjectRestResponse objectRestResponse = new ObjectRestResponse(403, "用户尚未登录");
                    objectRestResponse.setData(loginPath);
                    response.getWriter().write(JSON.toJSONString(objectRestResponse));
                    return false;
                }
                ThreadLocalUserId.set(userCode);
            }
        }

        return true;
    }

    public Triple<String, String, String> getToken(HttpServletRequest request) {
        String accessToken = null;
        String userCode = null;
        String initUserCode = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TOKEN_HEADER)) {
                    accessToken = cookie.getValue();
                } else if (cookie.getName().equals(TOKEN_USERCODE_HEADER)) {
                    userCode = cookie.getValue();
                } else if (cookie.getName().equals(TOKEN_USERCODE)) {
                    initUserCode = cookie.getValue();
                }
            }
        }
        return Triple.of(accessToken, userCode, initUserCode);
    }

*
     * 设置token
     *
     * @param token


    public void setToken(String token, String userCode, HttpServletResponse response) {
        Cookie cookie1 = new Cookie(TOKEN_HEADER, token);
        //设置cookie的有效路径
        cookie1.setPath("/");
        //种植cookie
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie(TOKEN_USERCODE_HEADER, userCode);
        cookie2.setPath("/");
        //种植cookie
        response.addCookie(cookie2);
    }

    public void removeToken(HttpServletResponse response) {
        Cookie cookie1 = new Cookie(TOKEN_HEADER, "");
        cookie1.setPath("/");//设置cookie的有效路径
        cookie1.setMaxAge(0);//立即删除
        //种植cookie
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie(TOKEN_USERCODE_HEADER, "");
        cookie2.setPath("/");//设置cookie的有效路径
        cookie2.setMaxAge(0);//立即删除
        //种植cookie
        response.addCookie(cookie2);

        Cookie cookie3 = new Cookie(TOKEN_USERCODE, "");
        cookie3.setPath("/");//设置cookie的有效路径
        cookie3.setMaxAge(0);//立即删除
        //种植cookie
        response.addCookie(cookie3);
    }
}

*/
