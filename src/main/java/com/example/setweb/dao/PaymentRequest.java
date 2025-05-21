package com.example.setweb.dao;

import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

/**
 * @author Yoruko
 */
public class PaymentRequest {
    private String paymentMethod;
    private String productName;
    private BigDecimal price;
    private String userName;
    private String options;
    private Integer count = 1;

    public PaymentRequest() {

    }


    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PaymentRequest(String paymentMethod, String productName, BigDecimal price, String userName, String options) {
        this.paymentMethod = paymentMethod;
        this.productName = productName;
        this.price = price;
        this.userName = userName;
        this.options = options;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "paymentMethod='" + paymentMethod + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", userName='" + userName + '\'' +
                ", options='" + options + '\'' +
                ", count=" + count +
                '}';
    }
}
