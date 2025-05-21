package com.example.setweb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.setweb.controller.PaymentController;
import com.example.setweb.dao.*;
import com.example.setweb.service.BankService;
import com.example.setweb.service.PaymentService;
import com.example.setweb.utils.BalanceUpdateExecutor;
import com.example.setweb.utils.RSASignature;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private BankService bankService;

    @Mock
    private BalanceUpdateExecutor balanceUpdateExecutor;

    @Mock
    private HttpSession session;

    @Mock
    private SecretKey secretKey;

    @Mock
    private KeyPair keyPair;

    @Mock
    private PrivateKey privateKey;

    @Mock
    private PublicKey publicKey;

    @Mock
    private X509Certificate certificate;

    @Mock
    private Future<BigDecimal> future;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequest paymentRequest;
    private PaymentResponse successResponse;
    private PaymentResponse insufficientBalanceResponse;
    private PaymentResponse nullUserResponse;
    private PaymentResponse updateFailedResponse;
    private PaymentResponse merchantVerifyFailedResponse;
    private PaymentResponse bankVerifyFailedResponse;

    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerTest.class);

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        paymentRequest = new PaymentRequest();
        paymentRequest.setUserName("testUser");
        paymentRequest.setProductName("Test Product");
        paymentRequest.setPrice(BigDecimal.valueOf(100));
        paymentRequest.setPaymentMethod("Credit Card");
        paymentRequest.setOptions("Option1");

        // 初始化各种响应
        successResponse = new PaymentResponse();
        successResponse.setCode(0);
        successResponse.setStatus("success");

        insufficientBalanceResponse = new PaymentResponse();
        insufficientBalanceResponse.setCode(1);
        insufficientBalanceResponse.setStatus("balance of user's account is not enough!");

        nullUserResponse = new PaymentResponse();
        nullUserResponse.setCode(2);
        nullUserResponse.setStatus("null user!");

        updateFailedResponse = new PaymentResponse();
        updateFailedResponse.setCode(3);
        updateFailedResponse.setStatus("balance update failed!");

        merchantVerifyFailedResponse = new PaymentResponse();
        merchantVerifyFailedResponse.setCode(4);
        merchantVerifyFailedResponse.setStatus("商家校验用户请求数据失败！");

        bankVerifyFailedResponse = new PaymentResponse();
        bankVerifyFailedResponse.setCode(3);
        bankVerifyFailedResponse.setStatus("银行校验用户请求数据失败！");

        // 模拟会话属性
        when(session.getAttribute("key")).thenReturn(secretKey);
        when(session.getAttribute("keyPair")).thenReturn(keyPair);
        when(session.getAttribute("cert")).thenReturn(certificate);
        when(keyPair.getPrivate()).thenReturn(privateKey);
        when(certificate.getPublicKey()).thenReturn(publicKey);
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(true);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.sendToMerchant(any(), any(), any())).thenReturn("sendToMerchant");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(true);
        when(balanceUpdateExecutor.submitSingle(any())).thenReturn(future);


//        when(future.get(anyLong(), any(TimeUnit.class))).thenReturn(BigDecimal.valueOf(900));

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(0, response.getCode());
        assertEquals("success", response.getStatus());

        // 验证交互
        verify(paymentService).initService(paymentRequest);
        verify(paymentService).encryptUserInfo(secretKey);
        verify(paymentService).generateDualSignature(privateKey);
        verify(paymentService).sendToMerchant(secretKey, any(KeyPair.class), certificate);
        verify(paymentService).receivedDataFromClient(any(), any(), any(), any(), any());
        verify(paymentService).bankVerify(any(PrivateKey.class));
        verify(balanceUpdateExecutor).submitSingle(any(PayInfo.class));
    }

    @Test
    void testProcessPayment_InsufficientBalance() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(false);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(true);

        Bank user = new Bank();
        user.setBalance(BigDecimal.valueOf(50));
        when(bankService.getUserByUsername(anyString())).thenReturn(user);

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(1, response.getCode());
        assertEquals("balance of user's account is not enough!", response.getStatus());
    }

    @Test
    void testProcessPayment_NullUser() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenThrow(new IllegalArgumentException("用户不存在"));
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(true);

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(2, response.getCode());
        assertEquals("null user!", response.getStatus());
    }

    @Test
    void testProcessPayment_MerchantVerifyFailed() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(true);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(false);
        when(paymentService.bankVerify(any())).thenReturn(true);

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(4, response.getCode());
        assertEquals("商家校验用户请求数据失败！", response.getStatus());
    }

    @Test
    void testProcessPayment_BankVerifyFailed() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(true);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(false);

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(3, response.getCode());
        assertEquals("银行校验用户请求数据失败！", response.getStatus());
    }

    @Test
    void testProcessPayment_BalanceUpdateFailed() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(true);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(true);
        when(balanceUpdateExecutor.submitSingle(any())).thenReturn(future);


//        when(future.get(anyLong(), any(TimeUnit.class))).thenThrow(new RuntimeException("Balance update failed"));

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(3, response.getCode());
        assertEquals("balance update failed!", response.getStatus());
    }

    @Test
    void testProcessPayment_EncryptionException() throws Exception {
        // 模拟加密异常
        when(paymentService.encryptUserInfo(any())).thenThrow(new RuntimeException("Encryption failed"));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            paymentController.processPayment(paymentRequest, session);
        });
    }

    @Test
    void testProcessPayment_AsyncTimeout() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(true);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(true);
        when(balanceUpdateExecutor.submitSingle(any())).thenReturn(future);


//        when(future.get(anyLong(), any(TimeUnit.class))).thenThrow(new java.util.concurrent.TimeoutException());

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(3, response.getCode());
        assertEquals("balance update failed!", response.getStatus());
    }

    @Test
    void testProcessPayment_MissingSessionAttributes() {
        // 重置会话模拟
        reset(session);
        when(session.getAttribute("key")).thenReturn(null);
        when(session.getAttribute("keyPair")).thenReturn(null);
        when(session.getAttribute("cert")).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalStateException.class, () -> {
            paymentController.processPayment(paymentRequest, session);
        });
    }

    @Test
    void testProcessPayment_InvalidPrice() throws Exception {
        // 设置无效价格
        paymentRequest.setPrice(BigDecimal.valueOf(-100));

        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenThrow(new IllegalArgumentException("价格不能为负数"));

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(2, response.getCode());
        assertEquals("null user!", response.getStatus());
    }

    @Test
    void testProcessPayment_ConcurrentModification() throws Exception {
        // 模拟服务层行为
        when(paymentService.checkAccountBalance(anyString(), any())).thenReturn(true);
        when(paymentService.generateDualSignature(any())).thenReturn("dualSignature");
        when(paymentService.receivedDataFromClient(any(), any(), any(), any(), any())).thenReturn(true);
        when(paymentService.bankVerify(any())).thenReturn(true);
        when(balanceUpdateExecutor.submitSingle(any())).thenReturn(future);


//        when(future.get(anyLong(), any(TimeUnit.class))).thenThrow(new java.util.concurrent.ExecutionException(new RuntimeException("Concurrent modification")));

        // 执行测试
        PaymentResponse response = paymentController.processPayment(paymentRequest, session);

        // 验证结果
        assertEquals(3, response.getCode());
        assertEquals("balance update failed!", response.getStatus());
    }
}
