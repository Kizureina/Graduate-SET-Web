package com.example.setweb.dao;

/**
 * @author Yoruko
 */
public class PayInfo {
    private String userName;
    private String paymentMethod;

    public PayInfo(String userName, String paymentMethod) {
        this.userName = userName;
        this.paymentMethod = paymentMethod;
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
                "userName='" + userName + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
