package com.example.setweb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {
    private static final Logger logger = LoggerFactory.getLogger(HashUtil.class);

    public static String sha1(String input) {
        try {
            // 获取 SHA-1 摘要实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }
            logger.info("生成" + input + "的SHA1哈希值");
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found!", e);
        }
    }

    /*
    * 返回二进制格式为base64的HMAC码
    * */
    public static String hmacSHA1(String message, String secretKey) {
        try {
            // 创建 HMAC-SHA1 实例
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            mac.init(secretKeySpec);

            // 计算 HMAC 值
            byte[] hmacBytes = mac.doFinal(message.getBytes());

            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hmacBytes) {
                hexString.append(String.format("%02x", b));
            }
            logger.info("成功生成" + message + "的HMAC校验码，返回二进制数据格式为Base64");
//            返回十六进制格式
//            return hexString.toString();
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (Exception e) {
            logger.error("HMAC报错，可能是密钥有问题");
            throw new RuntimeException("Failed to generate HMAC-SHA1 hash", e);
        }
    }
}
