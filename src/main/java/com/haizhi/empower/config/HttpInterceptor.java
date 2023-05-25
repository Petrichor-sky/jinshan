package com.haizhi.empower.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @description:
 * @author: wulugeng
 * @createDate: 2022/5/31
 * @version: 1.0
 */
public class HttpInterceptor implements ClientHttpRequestInterceptor {

    private Logger log = LoggerFactory.getLogger(HttpInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        log.debug("请求地址：{}", request.getURI());
        log.debug("请求方法： {}", request.getMethod());
        log.debug("请求内容：{}", new String(body));
        log.debug("请求头：{}", request.getHeaders());
        return execution.execute(request, body);
    }
}