package com.example.setweb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.setweb.dao.Bank;
import com.example.setweb.dao.OrderInfo;
import com.example.setweb.dao.PayInfo;
import com.example.setweb.dao.PaymentRequest;
import com.example.setweb.service.BankService;
import com.example.setweb.service.PaymentService;
import com.example.setweb.utils.DESUtil;
import com.example.setweb.utils.HashUtil;
import com.example.setweb.utils.RSASignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private BankService bankService;

    @Mock
    private PaymentRequest paymentRequest;

    @Mock
    private X509Certificate certificate;

    @Mock
    private KeyPair keyPair;

    @Mock
    private PrivateKey privateKey;

    @Mock
    private PublicKey publicKey;

    @Mock
    private SecretKey secretKey;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        when(paymentRequest.getUserName()).thenReturn("testUser");
        when(paymentRequest.getProductName()).thenReturn("Test Product");
        when(paymentRequest.getPrice()).thenReturn(BigDecimal.valueOf(100));
        when(paymentRequest.getPaymentMethod()).thenReturn("Credit Card");
        when(paymentRequest.getOptions()).thenReturn("Option1");

        paymentService.initService(paymentRequest);
    }

    @Test
    void testCheckAccountBalance_SufficientBalance() {
        Bank user = new Bank();
        user.setBalance(BigDecimal.valueOf(200));

        when(bankService.getUserByUsername("testUser")).thenReturn(user);

        assertTrue(paymentService.checkAccountBalance("testUser", BigDecimal.valueOf(100)));
    }

    @Test
    void testCheckAccountBalance_InsufficientBalance() {
        Bank user = new Bank();
        user.setBalance(BigDecimal.valueOf(50));

        when(bankService.getUserByUsername("testUser")).thenReturn(user);

        assertFalse(paymentService.checkAccountBalance("testUser", BigDecimal.valueOf(100)));
    }

    @Test
    void testCheckAccountBalance_UserNotFound() {
        when(bankService.getUserByUsername("unknownUser")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.checkAccountBalance("unknownUser", BigDecimal.valueOf(100)));
    }

    @Test
    void testUpdateUserBalance() {
        Bank user = new Bank();
        user.setBalance(BigDecimal.valueOf(200));

        when(bankService.getUserByUsername("testUser")).thenReturn(user);
        when(paymentRequest.getUserName()).thenReturn("testUser");

        PayInfo payInfo = new PayInfo("testUser", "Credit Card", BigDecimal.valueOf(50));
        BigDecimal newBalance = paymentService.updateUserBalance(payInfo);

        assertEquals(BigDecimal.valueOf(150), newBalance);
        verify(bankService).updateBalance("testUser", BigDecimal.valueOf(150));
    }

    @Test
    void testGenerateUserKey() throws Exception {
        SecretKey mockKey = mock(SecretKey.class);
        when(DESUtil.generateKey()).thenReturn(mockKey);

        SecretKey result = paymentService.generateUserKey();

        assertEquals(mockKey, result);
        verify(paymentRequest).getUserName();
    }

    @Test
    void testEncryptUserInfo() throws Exception {
        when(paymentRequest.toString()).thenReturn("testData");
        when(DESUtil.encrypt(anyString(), any())).thenReturn("encryptedData");

        String result = paymentService.encryptUserInfo(secretKey);

        assertEquals("encryptedData", result);
    }

    @Test
    void testGetPaymentInfoMsgDigest() {
        PayInfo payInfo = new PayInfo("testUser", "Credit Card", BigDecimal.valueOf(100));
        paymentService.setPayInfo(payInfo);

        when(HashUtil.sha1(payInfo.toString())).thenReturn("hashValue");

        String result = paymentService.getPaymentInfoMsgDigest();

        assertEquals("hashValue", result);
        assertEquals("hashValue", paymentService.getPayInfoMsgDigest());
    }

    @Test
    void testGetOrderInfoMsgDigest() {
        OrderInfo orderInfo = new OrderInfo("Test Product", "Option1");
        paymentService.setOrderInfo(orderInfo);

        when(HashUtil.sha1(orderInfo.toString())).thenReturn("orderHash");

        String result = paymentService.getOrderInfoMsgDigest();

        assertEquals("orderHash", result);
        assertEquals("orderHash", paymentService.getOrderInfoDigest());
    }

    @Test
    void testGetPaymentOrderMsgDigest() {
        when(paymentService.getPaymentInfoMsgDigest()).thenReturn("piHash");
        when(paymentService.getOrderInfoMsgDigest()).thenReturn("oiHash");
        when(HashUtil.sha1("piHash" + "oiHash")).thenReturn("poHash");

        String result = paymentService.getPaymentOrderMsgDigest();

        assertEquals("poHash", result);
    }

    @Test
    void testGenerateDualSignature() throws Exception {
        when(paymentService.getPaymentOrderMsgDigest()).thenReturn("poHash");
        when(RSASignature.sign("poHash", privateKey)).thenReturn("signature");

        String result = paymentService.generateDualSignature(privateKey);

        assertEquals("signature", result);
    }

    @Test
    void testSendToMerchant() throws Exception {
        when(paymentService.getPayInfo()).thenReturn(new PayInfo("testUser", "Credit Card", BigDecimal.valueOf(100)));
        when(paymentService.generateDualSignature(any())).thenReturn("dualSig");
        when(paymentService.getOrderInfoMsgDigest()).thenReturn("oiHash");
        when(DESUtil.encrypt(anyString(), any())).thenReturn("encryptedPart");

        // when(RSASignature.encrypt(any(), any())).thenReturn(new byte[]{1, 2, 3});

        when(keyPair.getPublic()).thenReturn(publicKey);
        when(certificate.getEncoded()).thenReturn(new byte[]{4, 5, 6});

        String result = paymentService.sendToMerchant(secretKey, keyPair, certificate);

        assertNotNull(result);
        assertTrue(result.contains("encryptedPart"));
    }

    @Test
    void testReceivedDataFromClient() throws Exception {
        when(certificate.getPublicKey()).thenReturn(publicKey);
        when(HashUtil.sha1(anyString())).thenReturn("expectedHash");
        when(RSASignature.verify("expectedHash", "dualSig", publicKey)).thenReturn(true);

        boolean result = paymentService.receivedDataFromClient(
                "encryptedData", "piHash", "orderInfo", "dualSig", certificate);

        assertTrue(result);
    }

    @Test
    void testBankVerify() throws Exception {
        // when(paymentService.getDigitalEnvelope()).thenReturn("envelope");

        when(Base64.getDecoder().decode("envelope")).thenReturn(new byte[]{1, 2, 3});

        // when(RSASignature.decrypt(any(), any())).thenReturn(secretKey);

        when(DESUtil.decrypt(anyString(), any())).thenReturn("decryptedData");
        when(paymentService.getPayInfo()).thenReturn(mock(PayInfo.class));

        boolean result = paymentService.bankVerify(privateKey);

        assertTrue(result);
    }
}