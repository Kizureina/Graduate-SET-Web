package com.example.setweb.utils;

import java.math.BigInteger;
import java.security.*;
import java.util.Date;

import com.example.setweb.controller.PaymentController;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * @author Yoruko
 */
public class X509Generator {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate generator(String username, KeyPair keyPair) throws Exception {
        // 1. 生成 RSA 密钥对，接收用户登录时生成的RSA密钥

        // 2. 构造证书的 subject 和 issuer（自签名）
        X500Name issuer = new X500Name("CN=" + username + ", O=MyOrg, C=CN");

        // 3. 设置有效期
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000));
        // 有效期1年

        // 4. 创建证书构建器
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                issuer,
                keyPair.getPublic()
        );

        // 5. 用私钥签名生成证书
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

        // 6. 输出证书（PEM 格式）
        logger.info("生成的 X.509v3 证书：");
        logger.info("-----BEGIN CERTIFICATE-----");
        logger.info(Base64.getEncoder().encodeToString(cert.getEncoded()));
        logger.info("-----END CERTIFICATE-----");

        // 7. 使用私钥对测试消息签名
        String message = "user=alice&timestamp=" + System.currentTimeMillis();
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(message.getBytes());
        byte[] sigBytes = signature.sign();

        logger.info("签名消息:");
        logger.info(message);
        logger.info("签名(Base64):");
        logger.info(Base64.getEncoder().encodeToString(sigBytes));

        return cert;
    }
}

