package com.example.setweb.utils;

import java.security.*;
import java.util.Base64;

public class RSASignature {
    // 生成 RSA 密钥对
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        // 使用 2048 位密钥
        return keyPairGen.generateKeyPair();
    }

    // 生成 RSA 数字签名
    public static String sign(String message, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }

    // 验证 RSA 数字签名
    public static boolean verify(String message, String signatureStr, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        byte[] signedBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signedBytes);
    }

}
