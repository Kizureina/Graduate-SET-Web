package com.example.setweb.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * X.509v3 证书工具类（支持 RSA 签名）
 * @author Yoruko
 */
public class X509CertificateUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 私有构造方法，禁止实例化
     */
    private X509CertificateUtils() {}

    /**
     * 生成 RSA 密钥对
     *
     * @param keySize 密钥长度（例如 2048）
     * @return 生成的 RSA 密钥对
     * @throws NoSuchAlgorithmException 如果 RSA 算法不可用
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成 X.509v3 证书
     *
     * @param issuerName        颁发者名称
     * @param subjectName       使用者名称
     * @param keyPair           密钥对（包含公钥和私钥）
     * @param startDate         证书生效日期
     * @param endDate           证书失效日期
     * @param signatureAlgorithm 签名算法（例如 "SHA256WithRSAEncryption"）
     * @return 生成的 X.509v3 证书
     * @throws OperatorCreationException 如果签名器创建失败
     * @throws CertificateException      如果证书生成失败
     */
    public static X509Certificate generateX509v3Certificate(
            X500Name issuerName,
            X500Name subjectName,
            KeyPair keyPair,
            Date startDate,
            Date endDate,
            String signatureAlgorithm) throws OperatorCreationException, CertificateException {
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName,
                BigInteger.valueOf(System.currentTimeMillis()),
                startDate,
                endDate,
                subjectName,
                keyPair.getPublic()
        );

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm)
                .setProvider("BC")
                .build(keyPair.getPrivate());

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(contentSigner));
    }

    /**
     * 验证证书签名
     *
     * @param certificate 需要验证的证书
     * @param publicKey   用于验证签名的公钥
     * @return 如果签名验证成功返回 true，否则返回 false
     */
    public static boolean verifyCertificate(X509Certificate certificate, PublicKey publicKey) {
        try {
            certificate.verify(publicKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}