package com.example.setweb.service;

import com.example.setweb.dao.*;
import com.example.setweb.utils.DESUtil;
import com.example.setweb.utils.HashUtil;
import com.example.setweb.utils.RSASignature;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.security.PrivateKey;

/**
 * @author Yoruko
 */
@Service
public class PaymentService {
    @Resource
    private BankService bankService;
    private PaymentRequest paymentRequest;
    private PayInfo payInfo;
    private OrderInfo orderInfo;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);


    // 构造函数
    public PaymentService() {
    }

    public void initService(PaymentRequest paymentRequest){
        logger.info("用户信息为" + paymentRequest.getUserName() + paymentRequest.getProductName() + paymentRequest.getPrice() + paymentRequest.getPaymentMethod());
        logger.info("完成PI和IO初始化");
        this.paymentRequest = paymentRequest;
        this.payInfo = new PayInfo(paymentRequest.getUserName(), paymentRequest.getPaymentMethod());
        this.orderInfo = new OrderInfo(paymentRequest.getProductName(), paymentRequest.getPrice());
    }

    // 比较用户余额与商品价格
    public boolean checkAccountBalance(String userName, BigDecimal price) {
        // 参数校验
        if (price.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new IllegalArgumentException("价格不能为负数: " + price);
        }

        // 获取用户信息
        Bank user = bankService.getUserByUsername(userName);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + userName);
        }

        // 获取余额并比较
        BigDecimal balance = user.getBalance();
        return balance.compareTo(price) > 0;
    }

    public BigDecimal updateUserBalance(){
        BigDecimal balance;
        balance = bankService.getUserByUsername(payInfo.getUserName()).getBalance();
        BigDecimal nowBalance = balance.subtract(orderInfo.getPrice());
        bankService.updateBalance(payInfo.getUserName(), nowBalance);
        return nowBalance;
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
