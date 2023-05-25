package com.haizhi.empower.config;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CristianWindy
 * @Description MinioClientConfig
 * @Date 7/3/19 上午10:32
 **/
@Configuration
public class MinioClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioClientConfig.class);

    @Autowired
    private MinioConfig minioConfig;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = null;
        try {
            LOGGER.info("MinioClient init...");
            try {
                minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            } catch (Exception e) {
                LOGGER.info("MinioClient Exception...");
            }
        } catch (Exception e) {
            LOGGER.error("MinioClientConfig_init_error:{}", e);
        }
        return minioClient;
    }


}
