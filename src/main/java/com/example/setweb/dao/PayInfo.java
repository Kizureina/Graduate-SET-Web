package com.example.setweb.dao;

/**
 * @author Yoruko
 */
public class PayInfo {
    private String paymentMethod;
    private String userName;

    public PayInfo(String paymentMethod, String userName) {
        this.paymentMethod = paymentMethod;
        this.userName = userName;
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

    @Override
    public String toString() {
        return "PayInfo{" +
                "paymentMethod='" + paymentMethod + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
