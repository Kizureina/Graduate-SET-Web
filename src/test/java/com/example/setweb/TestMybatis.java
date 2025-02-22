package com.example.setweb;

import com.example.setweb.dao.Bank;
import com.example.setweb.mapper.BankMapper;
import com.example.setweb.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)  // 启用 Spring 测试支持
@SpringBootTest  // 启动整个 Spring 上下文（包括数据库连接）
class BankServiceIT {

    @Autowired
    private BankService bankService;  // 注入 BankService

    @Autowired
    private BankMapper bankMapper;  // 直接注入 MyBatis Mapper，确保可用

    @Test
    void testDatabaseConnection() {
        // 测试数据库是否可连接
        List<Bank> users = bankService.getAllUsers();
        assertNotNull(users, "数据库连接失败，返回的用户列表为 null");
    }

    @Test
    void testInsertAndRetrieveUser() {
        // 插入新用户
        String testUsername = "test_user";
        BigDecimal initialBalance = new BigDecimal("500.00");

        bankService.addUser(testUsername, initialBalance);

        // 从数据库查询用户
        Bank retrievedUser = bankService.getUserByUsername(testUsername);
        assertNotNull(retrievedUser, "插入失败，未找到用户");
        assertEquals(testUsername, retrievedUser.getUsername());
        assertEquals(initialBalance, retrievedUser.getBalance());

        // 清理测试数据
        bankMapper.deleteUser(testUsername);  // 需要在 BankMapper 添加 deleteUser 方法
    }

    @Test
    void testUpdateBalance() {
        String testUsername = "test_user";
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal updatedBalance = new BigDecimal("750.00");

        // 先插入用户
        bankService.addUser(testUsername, initialBalance);

        // 更新用户余额
        bankService.updateBalance(testUsername, updatedBalance);

        // 查询用户并验证余额
        Bank updatedUser = bankService.getUserByUsername(testUsername);
        assertNotNull(updatedUser, "更新失败，用户未找到");
        assertEquals(updatedBalance, updatedUser.getBalance());

        // 清理测试数据
        bankMapper.deleteUser(testUsername);
    }
}
