package com.example.setweb.service;

import com.example.setweb.dao.PaymentRequest;
import com.example.setweb.utils.DESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

/**
 * @author Yoruko
 */
public class PaymentService {
    private final PaymentRequest paymentRequest;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRequest paymentRequest) {
        logger.info("用户信息为" + paymentRequest.getUserName() + paymentRequest.getProductName() + paymentRequest.getProductType() + paymentRequest.getPaymentMethod());
        this.paymentRequest = paymentRequest;
    }

    public SecretKey generateUserKey() throws Exception {
        logger.info("生成用户" + paymentRequest.getUserName() + "密钥");
        return DESUtil.generateKey();
    }

    public String encryptUserInfo(SecretKey secretKey) throws Exception {
        String userInfo = paymentRequest.toString();
        logger.info("返回用户" + paymentRequest.getUserName() + "支付信息的DES密文");
        return DESUtil.encrypt(userInfo, secretKey);
    }


    @Override
    public String toString() {
        return "PaymentService{" +
                "paymentRequest=" + paymentRequest +
                '}';
    }
}
