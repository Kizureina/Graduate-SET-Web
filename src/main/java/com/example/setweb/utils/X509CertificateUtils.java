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

    // 私有构造方法，禁止实例化
    private X509CertificateUtils() {}

    // 生成 RSA 密钥对
    public static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    // 生成 X.509v3 证书
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

    // 验证证书签名
    public static boolean verifyCertificate(X509Certificate certificate, PublicKey publicKey) {
        try {
            certificate.verify(publicKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}