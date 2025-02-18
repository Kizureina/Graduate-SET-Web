package com.example.setweb.dao;

public class Bank {
    String bankUsername;
    Integer userCount;

    public Bank(String bankUsername) {
        this.bankUsername = bankUsername;
    }

    public String getBankUsername() {
        return bankUsername;
    }

    public void setBankUsername(String bankUsername) {
        this.bankUsername = bankUsername;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "bankUsername='" + bankUsername + '\'' +
                ", userCount=" + userCount +
                '}';
    }
}
