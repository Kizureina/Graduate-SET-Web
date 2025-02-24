package com.example.setweb.dao;

/**
 * @author Yoruko
 */
public class OrderInfo {
    private String productName;
    private String productType;

    public OrderInfo(String productName, String productType) {
        this.productName = productName;
        this.productType =productType;
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

    @Override
    public String toString() {
        return "OrderInfo{" +
                "productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                '}';
    }
}
