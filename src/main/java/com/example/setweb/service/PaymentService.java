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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;

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
    private String payInfoEncrypted;
    private String payInfoMsgDigest;
    private String orderInfoDigest;
    private String PaymentOrderMsgDigest;
    private String digitalEnvelope;

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

    private final Object lock = new Object();
    // 全局锁（或改成基于用户名的锁池）

    public BigDecimal updateUserBalance() {
        // 通过加全局锁实现线程安全地更新银行账户余额
        synchronized (lock) {
            BigDecimal balance = bankService.getUserByUsername(payInfo.getUserName()).getBalance();
            BigDecimal nowBalance = balance.subtract(orderInfo.getPrice());
            bankService.updateBalance(payInfo.getUserName(), nowBalance);
            return nowBalance;
        }
    }

    public SecretKey generateUserKey() throws Exception {
        logger.info("生成用户" + paymentRequest.getUserName() + "对称加密算法DES密钥");
        return DESUtil.generateKey();
    }

    public String encryptUserInfo(SecretKey secretKey) throws Exception {
        String userInfo = paymentRequest.toString();
        logger.info("返回用户" + paymentRequest.getUserName() + "支付信息的DES密文");
        return DESUtil.encrypt(userInfo, secretKey);
    }

    public String getPaymentInfoMsgDigest(){
        this.setPayInfoMsgDigest(HashUtil.sha1(this.getPayInfo().toString()));
        return this.getPayInfoMsgDigest();
    }

    public String getOrderInfoMsgDigest(){
        this.setOrderInfoDigest(HashUtil.sha1(this.getOrderInfo().toString()));
        return this.getOrderInfoDigest();
    }

    public String getPaymentOrderMsgDigest(){
        logger.info("返回PI哈希和OI哈希拼接结果的Hash值");
        this.PaymentOrderMsgDigest  = HashUtil.sha1(getPaymentInfoMsgDigest() + getOrderInfoMsgDigest());
        return getPOMsgDigest();
    }

    public String getPOMsgDigest(){
        return this.PaymentOrderMsgDigest;
    }

    public String generateDualSignature(PrivateKey customerPrivateKey) throws Exception {
        logger.info("生成双重数字签名");
        return RSASignature.sign(getPaymentOrderMsgDigest(), customerPrivateKey);
    }

    // 发送给商家的数据
    public String sendToMerchant(SecretKey secretKey, KeyPair keyPair, X509Certificate certificate) throws Exception {
        String part1;
        String dualSignature = this.generateDualSignature(keyPair.getPrivate());

        // DES加密PI + 双重数字签名 + IOMD
        part1 = DESUtil.encrypt(this.getPayInfo().toString() + dualSignature + this.getOrderInfoMsgDigest(), secretKey);

        // 数字信封传递DES对称密钥的RSA公钥加密，使用Base64编码密文
//        byte[] encrypted = RSASignature.encrypt(secretKey.toString(), keyPair.getPublic()); 这种写法会遇到密钥长度问题
        byte[] encrypted = RSASignature.encrypt(secretKey.getEncoded(), keyPair.getPublic());

        String digitalEnvelope = Base64.getEncoder().encodeToString(encrypted);

        // 第二部分：PIMD + OI + 双重数字签名 + 用户证书
        String part2 = getPaymentInfoMsgDigest() + getOrderInfo().toString() + dualSignature + Base64.getEncoder().encodeToString(certificate.getEncoded());

        // 赋值发送给银行的数据和数字信封
        payInfoEncrypted = part1;
        this.digitalEnvelope = digitalEnvelope;

        // 最终发给商家的数据
        return part1 + digitalEnvelope + part2;
    }


    // 商家校验请求数据
    public boolean receivedDataFromClient(String payInfoEncrypted, String paymentInfoMsgDigest, String orderInfo, String dualSignature, X509Certificate certificate) throws Exception {
        String POMD = HashUtil.sha1(paymentInfoMsgDigest + HashUtil.sha1(orderInfo));

        // 从用户公钥证书中获取RSA公钥
        PublicKey publicKey = certificate.getPublicKey();

        // RSA公钥验证签名
        return RSASignature.verify(POMD, dualSignature, publicKey);
    }


    // 银行校验请求数据
    public boolean bankVerify(PrivateKey privateKey) throws Exception {
        // 私钥解密数字信封，获取DES密钥
        // 1. Base64 解码
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(this.digitalEnvelope);

        // 2. 使用 RSA 私钥解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 推荐 padding 方式
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);

        // 3. 将解密后的字节构造为 SecretKey 对象
        SecretKey secretKey = new SecretKeySpec(decryptedKeyBytes, "DES");
        // algorithm 如 "AES"

        // DES解密传给银行的支付数据
        String decrypted = DESUtil.decrypt(this.payInfoEncrypted, secretKey);
        logger.info(decrypted);

        String dualSignature = this.generateDualSignature(privateKey);
        String msg = this.getPayInfo().toString() + dualSignature + this.getOrderInfoMsgDigest();

        return msg.equals(decrypted);
//        SecretKey secretKey = RSASignature.decrypt(this.digitalEnvelope.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    @Override
    public String toString() {
        return "PaymentService{" +
                "paymentRequest=" + paymentRequest +
                '}';
    }

    public BankService getBankService() {
        return bankService;
    }

    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getPayInfoEncrypted() {
        return payInfoEncrypted;
    }

    public void setPayInfoEncrypted(String payInfoEncrypted) {
        this.payInfoEncrypted = payInfoEncrypted;
    }

    public String getPayInfoMsgDigest() {
        return payInfoMsgDigest;
    }

    public void setPayInfoMsgDigest(String payInfoMsgDigest) {
        this.payInfoMsgDigest = payInfoMsgDigest;
    }

    public void setOrderInfoDigest(String orderInfoMsgDigest) {
        this.orderInfoDigest = orderInfoMsgDigest;
    }
    public String getOrderInfoDigest(){
        return orderInfoDigest;
    }
}
