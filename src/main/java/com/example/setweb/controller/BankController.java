package com.example.setweb.controller;

import com.example.setweb.dao.Bank;
import com.example.setweb.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yoruko
 */
@RestController
@RequestMapping("/api/bank")
public class BankController {
    @Autowired
    private BankService bankService;

    @GetMapping("/users")
    public List<Bank> getAllUsers() {
        return bankService.getAllUsers();
    }

    @GetMapping("/user/{username}")
    public Bank getUserByUsername(@PathVariable String username) {
        return bankService.getUserByUsername(username);
    }

    @PostMapping("/user")
    public String addUser(@RequestParam String username, @RequestParam BigDecimal balance) {
        bankService.addUser(username, balance);
        return "User added!";
    }

    @PutMapping("/user/{username}")
    public String updateBalance(@PathVariable String username, @RequestParam BigDecimal balance) {
        bankService.updateBalance(username, balance);
        return "Balance updated!";
    }
}
