package com.example.setweb.utils;

import com.example.setweb.dao.PayInfo;
import com.example.setweb.service.PaymentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @author Yoruko
 */
@Component
public class BalanceUpdateExecutor {
    private final PaymentService paymentService;
    private final ExecutorService executor;

    public BalanceUpdateExecutor(PaymentService paymentService, int threadPoolSize) {
        this.paymentService = paymentService;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public Future<BigDecimal> submitSingle(PayInfo payInfo) {
        return executor.submit(() -> paymentService.updateUserBalance(payInfo));
    }


    public List<BigDecimal> updateBalancesInParallel(List<PayInfo> payInfoList)
            throws InterruptedException, ExecutionException {
        List<Callable<BigDecimal>> tasks = new ArrayList<>();
        for (PayInfo payInfo : payInfoList) {
            tasks.add(() -> paymentService.updateUserBalance(payInfo));
        }

        List<Future<BigDecimal>> futures = executor.invokeAll(tasks);
        List<BigDecimal> results = new ArrayList<>();
        for (Future<BigDecimal> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
