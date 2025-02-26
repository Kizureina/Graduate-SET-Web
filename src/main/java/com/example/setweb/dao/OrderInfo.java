package com.example.setweb.dao;

import java.math.BigDecimal;

/**
 * @author Yoruko
 */
public class OrderInfo {
    private String productName;
    private BigDecimal price;

    public OrderInfo(String productName, BigDecimal price) {
        this.productName = productName;
        this.price = price;
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

    @Override
    public String toString() {
        return "OrderInfo{" +
                "productName='" + productName + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
