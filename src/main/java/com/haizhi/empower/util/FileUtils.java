package com.haizhi.empower.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author CristianWindy
 * @date: 2022年02月25日10:34:04
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static final long MB = 1024 * 1024;

    public static final Set<String> FILE_PIC_EXT_FILTER = new HashSet<String>() {{
        // 图片类
        add("BMP");
        add("JPG");
        add("JPEG");
        add("PNG");
        add("GIF");
        add("jpg");
        add("jpeg");
        add("png");
    }};

    public static final Set<String> FILE_VIDEO_EXT_FILTER = new HashSet<String>() {{
        // 视频类
        add("avi");
        add("mpeg4");
        add("mp4");
        add("ogg");
    }};

    public static final Set<String> FILE_DOC_EXT_FILTER = new HashSet<String>() {{
        //文档类
        add("txt");
        add("doc");
        add("xls");
        add("ppt");
        add("docx");
        add("xlsx");
        add("pptx");
        add("java");
        add("pdf");
    }};



    private static final Set<String> FILE_EXT_FILTER = new HashSet<String>() {{
        // 图片类
        add("bmp");
        add("jpg");
        add("jpeg");
        add("png");
        add("gif");
        add("tiff");
        add("swf");
        // 视频类
        add("avi");
        add("mpeg4");
        add("mp4");
        add("ogg");
        // 压缩类
        add("zip");
        add("rar");
        add("exe");
        add("msi");
        //文档类
        add("txt");
        add("doc");
        add("xls");
        add("ppt");
        add("docx");
        add("xlsx");
        add("pptx");
        add("java");
        add("pdf");
    }};

    /**
     * describe: 进行文件上传的工具类
     */
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    public static void upload(MultipartFile file, String filePath) throws Exception {
        file.transferTo(new File(filePath));
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getPrefix(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String fileName(String prefix, String suffix) {
        String fileName = prefix + UUID.randomUUID().toString() + DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT_ON_SECOND_CLEAR, new Date());
        return DigestUtils.md5DigestAsHex(fileName.getBytes()) + "." + suffix;
    }


    public static boolean attachmentFilter(String ext) {
        if (!StringUtils.isEmpty(ext)) {
            return FILE_EXT_FILTER.contains(ext.toLowerCase());
        }
        return false;
    }

    /**
     * 保存文件，路径不存在则创建
     *
     * @param fileData 文件数据
     * @param path     文件路径
     * @return
     * @throws IOException 创建文件异常
     */
    public static Path saveFile(byte[] fileData, Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return Files.write(path, fileData);
    }


    /**
     * 保存文件，路径不存在则创建
     *
     * @param fileName 文件名
     * @param fileData 文件数据
     * @param filePath 文件路径
     * @return
     * @throws IOException 创建文件异常
     */
    public static Path saveFile(byte[] fileData, String filePath, String fileName) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return Files.write(Paths.get(path.toString(), fileName), fileData);
    }

    /**
     * 删除文件及子目录
     *
     * @param file
     */
    public static void fileRemove(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                fileRemove(f);
            }
        }
        file.delete();
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}