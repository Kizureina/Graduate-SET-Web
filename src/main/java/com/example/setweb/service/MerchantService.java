package com.example.setweb.service;

import java.security.cert.X509Certificate;

/**
 * @author Yoruko
 */
public class MerchantService extends PaymentService{
    String payInfoEncrypted;

    public MerchantService(String payInfoEncrypted) {
        this.payInfoEncrypted = payInfoEncrypted;
    }

}
