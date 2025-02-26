package com.example.setweb.controller;

import com.example.setweb.dao.PaymentRequest;
import com.example.setweb.dao.PaymentResponse;
import com.example.setweb.service.PaymentService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.math.BigDecimal;

/**
 * @author Yoruko
 */
@RestController
// 统一 API 前缀
@RequestMapping("/api")
public class PaymentController {
    @Resource
    private PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/pay")
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) throws Exception {
        // 处理支付逻辑
        logger.info("收到支付请求:");
        logger.info(request.toString());
        paymentService.initService(request);

        // 生成用户密钥
        SecretKey secretKey = paymentService.generateUserKey();
        // 加密用户数据
        String cipher = paymentService.encryptUserInfo(secretKey);

        PaymentResponse paymentResponse = new PaymentResponse();

        try{
            if(paymentService.checkAccountBalance(request.getUserName(), request.getPrice())){
                paymentResponse.setCode(0);
                paymentResponse.setStatus("success");

                // 更新用户余额
                BigDecimal nowBalance = paymentService.updateUserBalance();
                logger.info("当前余额为" + nowBalance);
            }else {
                paymentResponse.setCode(1);
                paymentResponse.setStatus("balance of user's account is not enough!");
            }
        }catch (IllegalArgumentException illegalArgumentException){
            // 处理用户未登录异常
            paymentResponse.setCode(2);
            paymentResponse.setStatus("null user!");
        }

        return paymentResponse;
    }
}
