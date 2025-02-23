package com.example.setweb.dao;

/**
 * @author Yoruko
 */
public class OrderInfo {
    private String productName;
    private String productId;
    private String productType;

    public OrderInfo(String productName, String productId, String productType) {
        this.productName = productName;
        this.productId = productId;
        this.productType =productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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
                ", productId='" + productId + '\'' +
                ", productType='" + productType + '\'' +
                '}';
    }
}
