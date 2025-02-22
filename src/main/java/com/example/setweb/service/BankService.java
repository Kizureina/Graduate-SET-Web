package com.example.setweb.service;

import com.example.setweb.dao.Bank;
import com.example.setweb.mapper.BankMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yoruko
 */
@Service
public class BankService {
    private final BankMapper bankMapper;

    @Autowired
    public BankService(BankMapper bankMapper) {
        this.bankMapper = bankMapper;
    }

    public List<Bank> getAllUsers() {
        return bankMapper.getAllUsers();
    }

    public Bank getUserByUsername(String username) {
        return bankMapper.getUserByUsername(username);
    }

    public void addUser(String username, BigDecimal balance) {
        Bank user = new Bank();
        user.setUsername(username);
        user.setBalance(balance);
        bankMapper.insertUser(user);
    }

    public void updateBalance(String username, BigDecimal balance) {
        bankMapper.updateBalance(username, balance);
    }
}
