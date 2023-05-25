package com.haizhi.empower.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/27
 * @Copyright (c) 2009-2022 All rights reserved.
 */
public class SecretUtil {
    //这个不要改,改了之后前后端不能交互
    private static String DES_SECRET = "20170527";

    private static DESKeySpec key = null;
    private static SecretKeyFactory keyFactory = null;
    private static Cipher encryptCipher = null;
    private static Cipher decryptCipher = null;

    static {
        try {
            key = new DESKeySpec(DES_SECRET.getBytes());
            keyFactory = SecretKeyFactory.getInstance("DES");
            encryptCipher = Cipher.getInstance("DES");
            decryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, keyFactory.generateSecret(key));
            decryptCipher.init(Cipher.DECRYPT_MODE, keyFactory.generateSecret(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //生成MD5
    public static String md5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    // 二进制转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if (num < 0) {
                num += 256;
            }
            if (num < 16) {
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

    public static String des(String content) {
        if (content != null && !content.equals("")) {
            try {
                // Encode the string into bytes using utf-8
                byte[] unencryptedByteArray = content.getBytes("UTF8");

                // Encrypt
                byte[] encryptedBytes = encryptCipher.doFinal(unencryptedByteArray);

                // Encode bytes to base64 to get a string
                byte[] encodedBytes = Base64.encodeBase64(encryptedBytes);

                return new String(encodedBytes);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String decodeDes(String encryption) {
        if (encryption != null && !encryption.equals("")) {
            try {
                // Encode bytes to base64 to get a string
                byte[] decodedBytes = Base64.decodeBase64(encryption.getBytes());

                // Decrypt
                byte[] unencryptedByteArray = decryptCipher.doFinal(decodedBytes);

                // Decode using utf-8
                return new String(unencryptedByteArray, "UTF8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String seqUuid() {
        return ObjectId.get().toHexString();
    }

    /**
     * 返回指定长度的随机字符串
     * @param Len
     * @return
     */
    public static String getRandomString(int Len) {

        String[] baseString = {"0", "1", "2", "3",
                "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e",
                "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o",
                "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y",
                "z", "A", "B", "C", "D",
                "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S",
                "T", "U", "V", "W", "X", "Y", "Z"};
        Random random = new Random();
        int length = baseString.length;
        String randomString = "";
        for (int i = 0; i < length; i++) {
            randomString += baseString[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr = "";
        for (int i = 0; i < Len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length() - 1));
        }
        return resultStr;
    }
}
