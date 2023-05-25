package com.haizhi.empower.util;

import com.google.api.client.util.Base64;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @Description
 * @Author zjh
 * @Date 2022/12/6
 */
public class FileBase64Utils {

    /**
     * 文件转Base64*/
    public static String convertFileToBase64(String imgPath) {
        byte[] data = null;
        // 读取文件字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            System.out.println("文件大小（字节）="+in.available());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组进行Base64编码，得到Base64编码的字符串
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Str = encoder.encode(data);
        return base64Str;
    }

    /**
     * 对字节数组字符串进行Base64解码并生成文件
     * @param fileStr 文件base64位数据
     * @param fileFilePath 保存文件全路径地址
     * @return
     */
    public static boolean generateBase64StringToFile(String fileStr,String fileFilePath){
        if (fileStr == null) //文件base64位数据为空
            return false;
        try
        {
            //Base64解码
            byte[] b = Base64.decodeBase64(fileStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成文件
            OutputStream out = new FileOutputStream(fileFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 对字节数组字符串进行Base64解码并生成字符流
     * @param fileStr 文件base64位数据
     * @return
     */
    public static InputStream generateBase64StringToStream(String fileStr) throws Exception {
        try
        {
            //Base64解码
            byte[] b = Base64.decodeBase64(fileStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //返回字节流
            return new ByteArrayInputStream(b);
        }
        catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

}
