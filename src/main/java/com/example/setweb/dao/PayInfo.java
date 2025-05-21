package com.example.setweb.dao;

import java.math.BigDecimal;

/**
 * @author Yoruko
 */
public class PayInfo {
    private String userName;
    private String paymentMethod;
    private BigDecimal price;

    public PayInfo(String userName, String paymentMethod, BigDecimal price) {
        this.userName = userName;
        this.paymentMethod = paymentMethod;
        this.price = price;
    }

    public PayInfo(){

    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PayInfo{" +
                "userName='" + userName + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
