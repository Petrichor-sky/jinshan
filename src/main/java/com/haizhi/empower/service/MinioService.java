package com.haizhi.empower.service;

import com.haizhi.empower.config.MinioConfig;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.haizhi.empower.util.FileUtils.FILE_PIC_EXT_FILTER;
import static com.haizhi.empower.util.FileUtils.FILE_VIDEO_EXT_FILTER;


/**
 * 文件存储服务
 *
 * @author CristianWindy
 * @Date 2022年02月25日10:32:20
 **/
@Service
public class MinioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioService.class);

    private static final String DEFAULT_SPLIT = "/";
    private static final String DEFAULT_URL_HTTP = "http://";
    private static final String DEFAULT_URL_HTTPS = "https://";
    private static final String DEFAULT_INDEX_OF = "?";

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * @param inputStream  输入流
     * @param bucketName   桶名
     * @param longFileName 文件存放在minio中的名称
     *                     <p>
     *                     bucketName: images
     *                     fileName: longFileName.jpg
     *                     </p>
     * @desc 上传文件到minio
     */
    public boolean upload(InputStream inputStream, String bucketName, String longFileName) {
        try {
            // 1.判断桶是否存在
            if (!minioClient.bucketExists(bucketName)) {
                LOGGER.info("The minio bucket not found.");
                minioClient.makeBucket(bucketName);
                LOGGER.info("The minio bucket create success.");
            }
            // 2.开始执行存储
            minioClient.putObject(bucketName, longFileName, inputStream, minioConfig.getDefaultContentType());
            return true;
        } catch (Exception e) {
            LOGGER.error("MinioService.upload Exception..", e);
        }
        return false;
    }

    /**
     * @param bucketName   桶名
     * @param longFileName 文件存放在minio中的名称
     *                     <p>
     *                     bucketName: testBucketName
     *                     longFileName: longFileName.jpg
     *                     </p>
     * @desc 获取文件访问地址，此方法访问流量走Nginx，链接为永久链接。
     */
    public String getAccessAddress(String bucketName, String longFileName) {
        String accessAddress = minioConfig.getAccessAddressPrefix() + bucketName + DEFAULT_SPLIT + longFileName;
        LOGGER.info("bucketName:{}; longFileName:{}; accessAddress:{}", bucketName, longFileName, accessAddress);
        return accessAddress;
    }


    /**
     * 批量获取短地址
     *
     * @param attachments
     * @return
     */
    public List<String> generateShortAttachments(List<String> attachments) {
        List<String> attachmentShortAccess = new ArrayList<>();
        if (attachments != null && attachments.size() > 0) {
            attachments.forEach(data -> {
                String shortAddress = getShortAddressFromRequest(data);
                attachmentShortAccess.add(shortAddress);
            });
        }

        return attachmentShortAccess;
    }

    /**
     * 批量获取长地址
     *
     * @param attachments
     * @return
     */
    public List<String> generateLongAttachments(List<String> attachments) {
        List<String> attachmentShortAccess = new ArrayList<>();
        if (attachments != null && attachments.size() > 0) {
            attachments.forEach(data -> {
                String shortAddress = getAccessAddressFromQuery(data);
                attachmentShortAccess.add(shortAddress);
            });
        }

        return attachmentShortAccess;
    }

    /**
     * 将文件的web访问地址剪切为短地址（可存到数据库的地址）
     *
     * @param accessAddress
     * @return
     */
    public String getShortAddressFromRequest(String accessAddress) {
        if (StringUtils.isEmpty(accessAddress)) {
            return "";
        }
        return accessAddress.replace(minioConfig.getAccessAddressPrefix(), "");
    }

    /**
     * 将文件的短地址拼接为web可访问地址
     *
     * @param shortAddress
     * @return
     */
    public String getAccessAddressFromQuery(String shortAddress) {
        if (StringUtils.isEmpty(shortAddress)) {
            return "";
        }
        return minioConfig.getAccessAddressPrefix() + shortAddress;
    }


    /**
     * @param address 桶名/文件名.拓展名
     * @return 签名url
     * <p>
     * testBucketName/longFileName.jpg
     * </p>
     * @desc 获取文件访问地址，此方法访问流量走minio服务，链接为临时链接。默认过期时间为一天。
     */
    public String getSignedUrl(String address) {
        try {
            String[] url = address.split(DEFAULT_SPLIT);
            return minioClient.presignedGetObject(url[0], url[1], minioConfig.getDefaultExpiredTime());
        } catch (Exception e) {
            LOGGER.error("MinioService.getSignedUrl Exception:{}", e);
            return null;
        }
    }

    /**
     * @param signedUrl 签名的访问地址
     * @return address
     * <p>
     * address示例:
     * http://ip:port/bucketName/longFileName.jpg
     * ?X-Amz-Algorithm=AWS4-HMAC-SHA256
     * &X-Amz-Credential=haizhi%2F20190306%2F%2Fs3%2Faws4_request
     * &X-Amz-Date=20190306T125350Z&X-Amz-Expires=604800
     * &X-Amz-SignedHeaders=host
     * &Signature=acb03e15e0d5ee04c708e2a6c678a0b0eb1c3fb29294d4ac5a1f9fea8a8f5485
     * </p>
     * @desc 获取address
     */
    public String getAddressBySignedUrl(String signedUrl) {
        try {
            return signedUrl.substring(
                    getBucketNameStartIndex(minioConfig.getEndpoint()),
                    signedUrl.indexOf(DEFAULT_INDEX_OF)
            );
        } catch (Exception e) {
            LOGGER.error("MinioService.getAddressBySignedUrl Exception:{}", e);
            return null;
        }
    }

    /**
     * @param bucketName   桶名
     * @param longFileName 文件名
     * @return 签名url
     * <p>
     * bucketName: testBucketName
     * longFileName: longFileName.jpg
     * </p>
     * @desc 获取文件访问地址，此方法访问流量走minio服务，链接为临时链接。默认过期时间为一天。
     */
    public String getSignedUrl(String bucketName, String longFileName) {
        try {
            return minioClient.presignedGetObject(bucketName, longFileName, minioConfig.getDefaultExpiredTime());
        } catch (Exception e) {
            LOGGER.error("MinioService.getSignedUrl Exception:{}", e);
            return null;
        }
    }

    /**
     * @param bucketName   桶名
     * @param longFileName 文件名
     * @param expiredTime  过期时间
     * @return 签名url
     * <p>
     * bucketName: testBucketName
     * longFileName: longFileName.jpg
     * expiredTime: 86400
     * </p>
     * @desc 获取文件访问地址，此方法访问流量走minio服务，链接为临时链接。过期时间自定义。
     */
    public String getSignedUrl(String bucketName, String longFileName, Integer expiredTime) {
        try {
            return minioClient.presignedGetObject(bucketName, longFileName, expiredTime);
        } catch (Exception e) {
            LOGGER.error("MinioService.getSignedUrl :{}", e);
            return null;
        }
    }

    /**
     * service内部调用方法，禁止外部访问
     *
     * @param endpoint client地址
     * @return /所在的位置
     */
    private int getBucketNameStartIndex(String endpoint) {
        int startLen;
        if (endpoint.contains(DEFAULT_URL_HTTP)) {
            startLen = DEFAULT_URL_HTTP.length();
        } else if (endpoint.contains(DEFAULT_URL_HTTPS)) {
            startLen = DEFAULT_URL_HTTPS.length();
        } else {
            throw new RuntimeException("MinioService.getBucketNameStartIndex err: The url denied...");
        }
        if (endpoint.lastIndexOf(DEFAULT_SPLIT) > startLen) {
            return endpoint.length();
        } else {
            return endpoint.length() + 1;
        }
    }


    public byte[] getFileBytes(String fileName, boolean isLogo) {

        String bucketName;
        if (isLogo) {
            bucketName = "pic";

        } else {
            bucketName = minioConfig.getWebAttachmentBucket();
        }
        try {
            InputStream inputStream = minioClient.getObject(bucketName, fileName);
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, buff.length)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            return swapStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream download(String bucket, String objectKey) throws Exception {
        return minioClient.getObject(bucket, objectKey);
    }

    public Integer getTypeByExtName(String suffix) {
        if(FILE_PIC_EXT_FILTER.contains(suffix.toLowerCase())){
            return 1;
        }else if(FILE_VIDEO_EXT_FILTER.contains(suffix.toLowerCase())){
            return 2;
        }else{
            return 3;

        }

    }
}
