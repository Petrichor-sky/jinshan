package com.haizhi.empower.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * @author CristianWindy
 * @ClassName: MapperConfiguration
 * @Description:tk动态mapper扫描
 * @date: 2018年8月16日 上午11:34:48
 * @Copyright: 2018  All rights reserved.
 */
@Configuration
public class MapperConfiguration implements EnvironmentAware {

    private String basePackage;

    @Bean("mapperScannerConfigurer")
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sessionFactory");
        mapperScannerConfigurer.setBasePackage(basePackage);
        return mapperScannerConfigurer;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.basePackage = environment.getProperty("mybatis.basePackage");
    }
}