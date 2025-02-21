package com.example.setweb.controller;

import com.example.setweb.dao.PaymentRequest;
import com.example.setweb.dao.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yoruko
 */
@RestController
// 统一 API 前缀
@RequestMapping("/api")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/pay")
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        // 处理支付逻辑
        logger.info("收到支付请求:");
        logger.info(request.toString());

        return new PaymentResponse(0, "success");
    }
}
