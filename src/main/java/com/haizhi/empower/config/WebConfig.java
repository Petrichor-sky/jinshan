package com.haizhi.empower.config;

import cn.bdp.joif.base.registry.JoifInterceptorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;


/**
 * app端 拦截器配置
 *
 * @author chenb
 */
@Configuration
public class WebConfig implements JoifInterceptorRegistry {


    @Bean
    public AppLoginInterceptor getAppLoginInterceptor(){
        return new AppLoginInterceptor();
    }
    @Override
    public void register(InterceptorRegistry registry) {
        registry.addInterceptor(getAppLoginInterceptor()).addPathPatterns("/api/app/base");
    }
}
