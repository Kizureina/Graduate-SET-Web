package com.example.setweb.controller;

import com.example.setweb.dao.Bank;
import com.example.setweb.dao.PaymentRequest;
import com.example.setweb.dao.PaymentResponse;
import com.example.setweb.service.BankService;
import com.example.setweb.service.PaymentService;
import com.example.setweb.utils.RSASignature;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author Yoruko
 */
@RestController
// 统一 API 前缀
@RequestMapping("/api")
public class PaymentController {
    @Resource
    private PaymentService paymentService;
    @Resource
    private BankService bankService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/pay")
    public PaymentResponse processPayment(@RequestBody PaymentRequest request, HttpSession session) throws Exception {
        // 处理支付逻辑
        logger.info("收到支付请求:");
        logger.info(request.toString());
        paymentService.initService(request);

        // 生成用户对称密钥
        SecretKey secretKey = (SecretKey) session.getAttribute("key");

        // 使用DES加密用户数据
        String cipher = paymentService.encryptUserInfo(secretKey);
        logger.info("PI哈希和OI哈希拼接结果的Hash值" + cipher);

        // 获取用户登录时生成的RSA私钥
        KeyPair keyPair = (KeyPair) session.getAttribute("keyPair");
        PrivateKey privateKey = keyPair.getPrivate();

        // 使用用户RSA私钥签名，获取*双重数字签名*
        String dualSignature = paymentService.generateDualSignature(keyPair.getPrivate());
        logger.info("双重数字签名：" + dualSignature);

        // 获取公钥证书
        X509Certificate certificate = (X509Certificate) session.getAttribute("cert");

        // 最终发给商家的数据
        String sendToMerchant = paymentService.sendToMerchant(secretKey, keyPair, certificate);
        logger.warn("发给商家的数据：" + sendToMerchant);

        // 商家验证数据
        boolean verify = paymentService.receivedDataFromClient(
                paymentService.getPayInfoEncrypted(),
                paymentService.getPayInfoMsgDigest(),
                paymentService.getOrderInfo().toString(),
                dualSignature,
                certificate
        );

        // 银行验证数据
        boolean flag = paymentService.bankVerify(privateKey);

        if(verify){
            logger.info("商家校验双重数字签名成功！");
        }else {
            logger.error("商家校验双重数字签名结果失败！");
        }

        if(flag){
            logger.info("银行校验数字信封成功！");
        }else {
            logger.error("银行校验数字信封失败！");
        }

        PaymentResponse paymentResponse = new PaymentResponse();

        try{
            if(paymentService.checkAccountBalance(request.getUserName(), request.getPrice()) && verify && flag){
                paymentResponse.setCode(0);
                paymentResponse.setStatus("success");

                // 更新用户余额
                BigDecimal nowBalance = paymentService.updateUserBalance();
                logger.info("当前余额为" + nowBalance);
            }else {
                paymentResponse.setCode(1);
                paymentResponse.setStatus("balance of user's account is not enough!");

                Bank user = bankService.getUserByUsername(request.getUserName());
                logger.warn("用户余额不足，当前余额为" + user.getBalance());
            }
        }catch (IllegalArgumentException illegalArgumentException){
            // 处理用户未登录异常
            paymentResponse.setCode(2);
            paymentResponse.setStatus("null user!");
        }

        if(!verify){
            paymentResponse.setCode(4);
            paymentResponse.setStatus("商家校验用户请求数据失败！");
        }

        if(!flag){
            paymentResponse.setCode(3);
            paymentResponse.setStatus("银行校验用户请求数据失败！");
        }

        return paymentResponse;
    }
}
