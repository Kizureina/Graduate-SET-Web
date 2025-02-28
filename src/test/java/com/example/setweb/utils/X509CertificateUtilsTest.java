package com.example.setweb.utils;

import com.example.setweb.utils.X509CertificateUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class X509CertificateUtilsTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 测试密钥对生成
    @Test
    void testGenerateRSAKeyPair() throws Exception {
        KeyPair keyPair = X509CertificateUtils.generateRSAKeyPair(2048);
        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
    }

    // 测试证书生成和签名验证
    @Test
    void testGenerateAndVerifyCertificate() throws Exception {
        // 生成密钥对
        KeyPair keyPair = X509CertificateUtils.generateRSAKeyPair(2048);

        // 设置证书参数
        X500Name issuer = new X500Name("CN=Test CA");
        X500Name subject = new X500Name("CN=Test User");
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 365 * 24 * 60 * 60 * 1000L);

        // 生成证书
        X509Certificate certificate = X509CertificateUtils.generateX509v3Certificate(
                issuer,
                subject,
                keyPair,
                startDate,
                endDate,
                "SHA256WithRSAEncryption"
        );

        // 验证证书签名
        assertTrue(X509CertificateUtils.verifyCertificate(certificate, keyPair.getPublic()));
    }

    // 测试无效签名验证
    @Test
    void testInvalidSignature() throws Exception {
        KeyPair keyPair1 = X509CertificateUtils.generateRSAKeyPair(2048);
        KeyPair keyPair2 = X509CertificateUtils.generateRSAKeyPair(2048);

        X500Name issuer = new X500Name("CN=Test CA");
        X500Name subject = new X500Name("CN=Test User");
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 365 * 24 * 60 * 60 * 1000L);

        X509Certificate certificate = X509CertificateUtils.generateX509v3Certificate(
                issuer,
                subject,
                keyPair1,
                startDate,
                endDate,
                "SHA256WithRSAEncryption"
        );

        // 使用错误的公钥验证
        assertFalse(X509CertificateUtils.verifyCertificate(certificate, keyPair2.getPublic()));
    }
}
