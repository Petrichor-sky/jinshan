package com.haizhi.empower.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: wulugeng
 * @createDate: 2021/5/19
 * @version: 1.0
 */
@Configuration
public class RestTemplateConfig {


    @Bean(name ="restTemplate1")
    public RestTemplate restTemplate1(ClientHttpRequestFactory factory){
        RestTemplate restTemplate=new RestTemplate(new BufferingClientHttpRequestFactory(factory));
        restTemplate.getInterceptors().add(new HttpInterceptor());
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        //return new RestTemplate(factory);
        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }


    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(150000);
        factory.setReadTimeout(50000);
        return factory;
    }
}
