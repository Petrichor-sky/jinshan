package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.config.MinioConfig;
import com.haizhi.empower.entity.po.Attachment;
import com.haizhi.empower.entity.vo.AttachmentVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.exception.ServiceException;
import com.haizhi.empower.mapper.AttachmentMapper;
import com.haizhi.empower.util.FileBase64Utils;
import com.haizhi.empower.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static java.util.Objects.isNull;


/**
 * 附件管理表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Slf4j
@Service
public class AttachmentService extends BaseService<AttachmentMapper, Attachment> {
    @Autowired
    private MinioService minioService;
    @Autowired
    private MinioConfig minioConfig;

    public Map<String, Object> attachUpload(MultipartFile file, Integer type, boolean isSigned) {
        try {
            // 文件判空
            if (isNull(file) || file.isEmpty()) {
                throw new RuntimeException("文件为空");
            }
            // 文件全名
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("无法获取当前文件名称");
            }

            // 文件前后缀
            String prefix = FileUtils.getPrefix(fileName);
            String suffix = FileUtils.getSuffix(fileName);
            log.info("file:{}; prefix:{}; suffix:{}", fileName, prefix, suffix);

            // 过滤文件后缀是否符合规格
           /* if (!FileUtils.attachmentFilter(suffix)) {
                throw new RuntimeException("文件拓展名不符合规则");
            }*/

            // 文件大小校验
            long fileSize = file.getSize();

            // 真实存储的文件
            String orgFileName = FileUtils.fileName(prefix, suffix);

//            String bucket = UploadTypeEnum.ICON.getCode().equals(type) ? minioConfig.getIconBucket() : minioConfig.getWebAttachmentBucket();
            String bucket = minioConfig.getWebAttachmentBucket();
            // 文件上传到minio
            boolean ret = minioService.upload(file.getInputStream(), bucket, orgFileName);
            if (!ret) {
                throw new RuntimeException("文件上传异常");
            }

            log.info("文件上传成功...");

            String address = isSigned ?
                    minioService.getSignedUrl(bucket, orgFileName)  // 获取签名的分享链接，走minio流量
                    :
                    minioService.getAccessAddress(bucket, orgFileName);  // 获取访问链接，走nginx流量


            Attachment attachment = Attachment.builder()
                    .address(minioService.getShortAddressFromRequest(address))
                    .size(fileSize)
                    .type(minioService.getTypeByExtName(suffix))
                    .name(prefix)
                    .extName(suffix)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            insert(attachment);
            Map<String, Object> retMap = new HashMap<String, Object>() {{
                put("size", fileSize);
                put("name", prefix);
                put("address", address);
                put("extName", suffix);
                put("id", attachment.getId());
            }};
            log.info("retMap: {}", retMap);
            return retMap;

        } catch (Exception e) {
            log.error("AttachmentUploadServiceImpl.attachUpload Exception:{}", e.getMessage());
            throw new ServiceException("文件上传异常:" + e.getMessage());
        }
    }

    /**
     * 多文件上传*/
    public BaseResponse attachUploads(MultipartFile[] files) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // 文件判空
                if (isNull(file) || file.isEmpty()) {
                    throw new RuntimeException("文件为空");
                }
                // 文件全名
                String fileName = file.getOriginalFilename();
                if (StringUtils.isBlank(fileName)) {
                    throw new RuntimeException("无法获取当前文件名称");
                }

                // 文件前后缀
                String prefix = FileUtils.getPrefix(fileName);
                String suffix = FileUtils.getSuffix(fileName);
                log.info("file:{}; prefix:{}; suffix:{}", fileName, prefix, suffix);

                // 过滤文件后缀是否符合规格
           /* if (!FileUtils.attachmentFilter(suffix)) {
                throw new RuntimeException("文件拓展名不符合规则");
            }*/

                // 文件大小校验
                long fileSize = file.getSize();

                // 真实存储的文件
                String orgFileName = FileUtils.fileName(prefix, suffix);

//            String bucket = UploadTypeEnum.ICON.getCode().equals(type) ? minioConfig.getIconBucket() : minioConfig.getWebAttachmentBucket();
                String bucket = minioConfig.getWebAttachmentBucket();
                // 文件上传到minio
                boolean ret = minioService.upload(file.getInputStream(), bucket, orgFileName);
                if (!ret) {
                    throw new RuntimeException("文件上传异常");
                }

                log.info("文件上传成功...");

                String address = minioService.getAccessAddress(bucket, orgFileName);  // 获取访问链接，走nginx流量


                Attachment attachment = Attachment.builder()
                        .address(minioService.getShortAddressFromRequest(address))
                        .size(fileSize)
                        .type(minioService.getTypeByExtName(suffix))
                        .name(prefix)
                        .extName(suffix)
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                insert(attachment);
                Map<String, Object> retMap = new HashMap<String, Object>() {{
                    put("size", fileSize);
                    put("name", prefix);
                    put("address", address);
                    put("extName", suffix);
                    put("id", attachment.getId());
                }};
                log.info("retMap: {}", retMap);
                results.add(retMap);

            } catch (Exception e) {
                log.error("AttachmentUploadServiceImpl.attachUpload Exception:{}", e.getMessage());
                throw new ServiceException("文件上传异常:" + e.getMessage());
            }
        }
        return new ObjectRestResponse<>().data(results);
    }

    /**
     * app端 base64转文件上传*/
    public BaseResponse appBase64upload(Map params) {
        try {
            String fileStr = params.get("fileStr").toString();
            String prefixName = params.get("prefixName").toString();
            String suffixName = params.get("suffixName").toString();

            // 过滤文件后缀是否符合规格
            if (!FileUtils.attachmentFilter(suffixName)) {
                throw new RuntimeException("文件拓展名不符合规则");
            }

            // 真实存储的文件
            String orgFileName = FileUtils.fileName(prefixName, suffixName);
            String bucket = minioConfig.getWebAttachmentBucket();
            // 文件上传到minio
            boolean ret = minioService.upload(FileBase64Utils.generateBase64StringToStream(fileStr), bucket, orgFileName);
            if (!ret) {
                throw new RuntimeException("文件上传异常");
            }

            System.err.println("文件上传成功...");

            String address = minioService.getAccessAddress(bucket, orgFileName);  // 获取访问链接，走nginx流量

            Attachment attachment = Attachment.builder()
                    .address(minioService.getShortAddressFromRequest(address))
                    .type(minioService.getTypeByExtName(suffixName))
                    .name(prefixName)
                    .extName(suffixName)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            insert(attachment);

            Map<String, Object> retMap = new HashMap<String, Object>() {{
                put("address", address);
                put("prefixName", prefixName);
                put("suffixName", suffixName);
                put("attachmentId", attachment.getId());
            }};
            System.err.println("retMap: " + retMap);
            return new ObjectRestResponse<>().data(retMap);

        } catch (Exception e) {
            System.err.println("AttachmentUploadServiceImpl.attachUpload Exception:" + e.getMessage());
            throw new ServiceException("文件上传异常:" + e.getMessage());
        }
    }

    public Map<String, Object> formDataUpload(MultipartFile file, Integer type, boolean isSigned) {
        try {
            // 文件判空
            if (isNull(file) || file.isEmpty()) {
                throw new RuntimeException("文件为空");
            }
            // 文件全名
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("无法获取当前文件名称");
            }

            // 文件前后缀
            String prefix = FileUtils.getPrefix(fileName);
            String suffix = FileUtils.getSuffix(fileName);
            log.info("file:{}; prefix:{}; suffix:{}", fileName, prefix, suffix);

            // 过滤文件后缀是否符合规格
           /* if (!FileUtils.attachmentFilter(suffix)) {
                throw new RuntimeException("文件拓展名不符合规则");
            }*/

            // 文件大小校验
            long fileSize = file.getSize();

            // 真实存储的文件
            String orgFileName = FileUtils.fileName(prefix, suffix);

//            String bucket = UploadTypeEnum.ICON.getCode().equals(type) ? minioConfig.getIconBucket() : minioConfig.getWebAttachmentBucket();
            String bucket = minioConfig.getFormDataBucket();
            // 文件上传到minio
            boolean ret = minioService.upload(file.getInputStream(), bucket, orgFileName);
            if (!ret) {
                throw new RuntimeException("文件上传异常");
            }

            log.info("文件上传成功...");

            String address = isSigned ?
                    minioService.getSignedUrl(bucket, orgFileName)  // 获取签名的分享链接，走minio流量
                    :
                    minioService.getAccessAddress(bucket, orgFileName);  // 获取访问链接，走nginx流量


            Attachment attachment = Attachment.builder()
                    .address(minioService.getShortAddressFromRequest(address))
                    .size(fileSize)
                    .type(minioService.getTypeByExtName(suffix))
                    .name(prefix)
                    .extName(suffix)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            insert(attachment);
            Map<String, Object> retMap = new HashMap<String, Object>() {{
                //put("size", fileSize);
                //put("name", prefix);
                put("url", address);
                //put("extName", suffix);
                put("fileId", attachment.getId());
            }};
            log.info("retMap: {}", retMap);
            return retMap;

        } catch (Exception e) {
            log.error("AttachmentUploadServiceImpl.attachUpload Exception:{}", e.getMessage());
            throw new ServiceException("文件上传异常:" + e.getMessage());
        }
    }

    public Map<String, Object> attachUploadWithOriginName(MultipartFile file, Integer type, boolean isSigned) {
        try {
            // 文件判空
            if (isNull(file) || file.isEmpty()) {
                throw new RuntimeException("文件为空");
            }
            // 文件全名
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("无法获取当前文件名称");
            }

            // 文件前后缀
            String prefix = FileUtils.getPrefix(fileName);
            String suffix = FileUtils.getSuffix(fileName);
            log.info("file:{}; prefix:{}; suffix:{}", fileName, prefix, suffix);

            // 过滤文件后缀是否符合规格
            if (!FileUtils.attachmentFilter(suffix)) {
                throw new RuntimeException("文件拓展名不符合规则");
            }

            // 文件大小校验
            long fileSize = file.getSize();

            // 真实存储的文件
            //String orgFileName = FileUtils.fileName(prefix, suffix);

//            String bucket = UploadTypeEnum.ICON.getCode().equals(type) ? minioConfig.getIconBucket() : minioConfig.getWebAttachmentBucket();
            String bucket = minioConfig.getWebAttachmentBucket();
            // 文件上传到minio
            boolean ret = minioService.upload(file.getInputStream(), bucket, fileName);
            if (!ret) {
                throw new RuntimeException("文件上传异常");
            }

            log.info("文件上传成功...");

            String address = isSigned ?
                    minioService.getSignedUrl(bucket, fileName)  // 获取签名的分享链接，走minio流量
                    :
                    minioService.getAccessAddress(bucket, fileName);  // 获取访问链接，走nginx流量

            Map<String, Object> retMap = new HashMap<String, Object>() {{
                put("size", fileSize);
                put("name", prefix);
                put("address", address);
                put("extName", suffix);
            }};
            log.info("retMap: {}", retMap);

            return retMap;

        } catch (Exception e) {
            log.error("AttachmentUploadServiceImpl.attachUpload Exception:{}", e.getMessage());
            throw new ServiceException("文件上传异常:" + e.getMessage());
        }
    }

    public Map<String, Object> exportExcelUpload(MultipartFile file, boolean isSigned) {
        try {
            // 文件判空
            if (isNull(file) || file.isEmpty()) {
                throw new RuntimeException("文件为空");
            }
            // 文件全名
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("无法获取当前文件名称");
            }

            // 文件前后缀
            String prefix = FileUtils.getPrefix(fileName);
            String suffix = FileUtils.getSuffix(fileName);
            log.info("file:{}; prefix:{}; suffix:{}", fileName, prefix, suffix);

            // 文件大小校验
            long fileSize = file.getSize();

            // 真实存储的文件
            String orgFileName = FileUtils.fileName(prefix, suffix);

            String bucket = minioConfig.getExportExcel();
            // 文件上传到minio
            boolean ret = minioService.upload(file.getInputStream(), bucket, orgFileName);
            if (!ret) {
                throw new RuntimeException("文件上传异常");
            }

            log.info("文件上传成功...");

            String address = isSigned ?
                    minioService.getSignedUrl(bucket, orgFileName)  // 获取签名的分享链接，走minio流量
                    :
                    minioService.getAccessAddress(bucket, orgFileName);  // 获取访问链接，走nginx流量


            Attachment attachment = Attachment.builder()
                    .address(minioService.getShortAddressFromRequest(address))
                    .size(fileSize)
                    .type(minioService.getTypeByExtName(suffix))
                    .name(prefix)
                    .extName(suffix)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            insert(attachment);
            Map<String, Object> retMap = new HashMap<String, Object>() {{
                put("size", fileSize);
                put("name", prefix);
                put("url", minioService.getShortAddressFromRequest(address));
                put("extName", suffix);
                put("fileId", attachment.getId());
            }};
            log.info("retMap: {}", retMap);
            return retMap;

        } catch (Exception e) {
            log.error("AttachmentUploadServiceImpl.attachUpload Exception:{}", e.getMessage());
            throw new ServiceException("文件上传异常:" + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAttachmentsByIds(ArrayList<Integer> ids) {
        List<Map<String, Object>> attachments = new ArrayList<>();
        if (!ObjectUtils.isEmpty(ids)) {
            ids.forEach(data -> {
                Attachment attachment = selectOne(Attachment.builder()
                        .id(data.longValue())
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .build());
                if (attachment != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", data);
                    map.put("extName", attachment.getExtName());
                    map.put("name", attachment.getName() + "." + attachment.getExtName());
                    map.put("address", minioService.getAccessAddressFromQuery(attachment.getAddress()));
                    attachments.add(map);
                }
            });
        }
        return attachments;
    }

    public String getAttachmentById(Long id) {
        Attachment attachment = selectOne(Attachment.builder()
                .id(id)
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());
        if (attachment != null) {
            return minioService.getAccessAddressFromQuery(attachment.getAddress());
        } else {
            return "";
        }
    }

    public BaseResponse deleteAttachment(Long id) {

        Attachment attachment = selectOne(Attachment.builder().id(id).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (attachment == null) {
            throw new RestException("附件Code为：" + id + "的用户不存在");
        }
        attachment.setDr(DeleteStatusEnum.DEL_YES.getCode());
        attachment.setUpdateTime(new Date());
        updateById(attachment);
        return new BaseResponse();
    }

    /**
     * 根据文件名获取字节数组*/
    public byte[] getByteByFileName(String fileSuffixPath) {
        String[] str = fileSuffixPath.split("/");
        String filename = str[1];
        log.info("filename：" + filename);
        return minioService.getFileBytes(filename, false);
    }

    //附件地址转为长地址
    public List<AttachmentVo> conversionLongAddress(List<AttachmentVo> vos) {
        vos.forEach(vo -> {
            vo.setAddress(minioService.getAccessAddressFromQuery(vo.getAddress()));
        });
        return vos;
    }

    /**
     * 根据附件id列表查询
     * @param ids id列表
     * @return
     */
    public List<AttachmentVo> selectByIds(List<Long> ids){
        return mapper.selectByIds(ids);
    }
}