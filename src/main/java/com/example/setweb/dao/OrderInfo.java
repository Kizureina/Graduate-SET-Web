package com.example.setweb.dao;

import java.math.BigDecimal;

/**
 * @author Yoruko
 */
public class OrderInfo {
    private String productName;
    private Integer count = 1;

    private String options;

    public OrderInfo(String productName, String options) {
        this.productName = productName;
        this.options = options;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "productName='" + productName + '\'' +
                ", count=" + count +
                ", options='" + options + '\'' +
                '}';
    }
}
