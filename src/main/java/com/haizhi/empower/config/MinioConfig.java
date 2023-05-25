package com.haizhi.empower.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 分布式文件管理系统minio客户端配置
 *
 * @author CristianWindy
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String defaultContentType;
    private String accessAddressPrefix;
    private Integer defaultExpiredTime;
    private String webAttachmentBucket;
    private String formDataBucket;
    private String exportExcel;
    private String iconBucket;
    private String configureResourcesBucket;

}