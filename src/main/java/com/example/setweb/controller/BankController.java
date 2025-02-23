package com.example.setweb.controller;

import com.example.setweb.dao.Bank;
import com.example.setweb.service.BankService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yoruko
 */
@RestController
@RequestMapping("/api/bank")
public class BankController {
    private static final Logger logger = LoggerFactory.getLogger(BankController.class);

    @Resource
    private BankService bankService;

    @GetMapping("/users")
    public List<Bank> getAllUsers() {
        logger.info("返回银行所有用户");
        return bankService.getAllUsers();
    }

    @GetMapping("/user/{username}")
    public Bank getUserByUsername(@PathVariable String username) {
        logger.info("基于用户名" + username + "查询银行账户余额");
        return bankService.getUserByUsername(username);
    }

    @PostMapping("/user")
    public String addUser(@RequestParam String username, @RequestParam BigDecimal balance) {
        bankService.addUser(username, balance);
        logger.info("添加用户" + username + "余额为" + balance);
        return "User added!";
    }

    @PutMapping("/user/{username}")
    public String updateBalance(@PathVariable String username, @RequestParam BigDecimal balance) {
        bankService.updateBalance(username, balance);
        logger.info("更新用户" + username + "余额更新为" + balance);
        return "Balance updated!";
    }
}
