package com.example.setweb.dao;

/**
 * @author Yoruko
 */
public class PaymentRequest {
    private String paymentMethod;
    private String productName;
    private String productType;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public PaymentRequest(String paymentMethod, String productName, String productType, String userName) {
        this.paymentMethod = paymentMethod;
        this.productName = productName;
        this.productType = productType;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "paymentMethod='" + paymentMethod + '\'' +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
