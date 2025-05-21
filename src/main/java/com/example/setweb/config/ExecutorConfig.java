package com.example.setweb.config;

import com.example.setweb.service.PaymentService;
import com.example.setweb.utils.BalanceUpdateExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yoruko
 */
@Configuration
public class ExecutorConfig {
    @Bean
    public BalanceUpdateExecutor balanceUpdateExecutor(PaymentService paymentService) {
        return new BalanceUpdateExecutor(paymentService, 10);
        // 手动传入线程池大小
    }
}

