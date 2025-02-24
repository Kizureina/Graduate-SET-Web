package com.example.setweb.service;

import com.example.setweb.dao.OrderInfo;
import com.example.setweb.dao.PayInfo;
import com.example.setweb.dao.PaymentRequest;
import com.example.setweb.utils.DESUtil;
import com.example.setweb.utils.HashUtil;
import com.example.setweb.utils.RSASignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

/**
 * @author Yoruko
 */
public class PaymentService {
    private final PaymentRequest paymentRequest;
    private PayInfo payInfo;
    private OrderInfo orderInfo;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public void initService(PaymentRequest paymentRequest){
        this.payInfo = new PayInfo(paymentRequest.getUserName(), paymentRequest.getPaymentMethod());
        this.orderInfo = new OrderInfo(paymentRequest.getProductName(), paymentRequest.getProductType());
    }

    public PaymentService(PaymentRequest paymentRequest) {
        logger.info("用户信息为" + paymentRequest.getUserName() + paymentRequest.getProductName() + paymentRequest.getProductType() + paymentRequest.getPaymentMethod());
        initService(paymentRequest);
        logger.info("完成PI和IO初始化");
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

    public String getPaymentInfoMsgDigest(){
        return HashUtil.sha1(this.payInfo.toString());
    }

    public String getOrderInfoMsgDigest(){
        return HashUtil.sha1(this.orderInfo.toString());
    }

    public String getPaymentOrderMsgDigest(){
        logger.info("返回PI哈希和OI哈希拼接结果的Hash值");
        return HashUtil.sha1(getPaymentInfoMsgDigest() + getOrderInfoMsgDigest());
    }

    public String generateDualSignature(PrivateKey customerPrivateKey) throws Exception {
        logger.info("生成双重数字签名");
        return RSASignature.sign(getPaymentOrderMsgDigest(), customerPrivateKey);
    }

    @Override
    public String toString() {
        return "PaymentService{" +
                "paymentRequest=" + paymentRequest +
                '}';
    }
}
