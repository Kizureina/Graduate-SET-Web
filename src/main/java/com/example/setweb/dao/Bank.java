package com.example.setweb.dao;

import java.math.BigDecimal;

public class Bank {
    private String username;
    private BigDecimal balance;

    // 构造方法
    public Bank() {}

    public Bank(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    // Getter 和 Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BankUser{" +
                "username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }
}

