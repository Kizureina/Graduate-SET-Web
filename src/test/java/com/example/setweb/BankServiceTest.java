package com.example.setweb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.setweb.dao.Bank;
import com.example.setweb.mapper.BankMapper;
import com.example.setweb.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @Mock
    private BankMapper bankMapper;

    @InjectMocks
    private BankService bankService;

    private Bank testUser1;
    private Bank testUser2;
    private List<Bank> userList;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser1 = new Bank();
        testUser1.setUsername("user1");
        testUser1.setBalance(BigDecimal.valueOf(1000));

        testUser2 = new Bank();
        testUser2.setUsername("user2");
        testUser2.setBalance(BigDecimal.valueOf(2000));

        userList = Arrays.asList(testUser1, testUser2);
    }

    @Test
    void testGetAllUsers() {
        // 模拟Mapper行为
        when(bankMapper.getAllUsers()).thenReturn(userList);

        // 调用服务方法
        List<Bank> result = bankService.getAllUsers();

        // 验证结果
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals(BigDecimal.valueOf(1000), result.get(0).getBalance());
        assertEquals("user2", result.get(1).getUsername());
        assertEquals(BigDecimal.valueOf(2000), result.get(1).getBalance());

        // 验证交互
        verify(bankMapper).getAllUsers();
        verifyNoMoreInteractions(bankMapper);
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // 模拟空列表
        when(bankMapper.getAllUsers()).thenReturn(List.of());

        // 调用服务方法
        List<Bank> result = bankService.getAllUsers();

        // 验证结果
        assertTrue(result.isEmpty());
        verify(bankMapper).getAllUsers();
    }

    @Test
    void testGetUserByUsername_Exists() {
        // 模拟Mapper行为
        when(bankMapper.getUserByUsername("user1")).thenReturn(testUser1);

        // 调用服务方法
        Bank result = bankService.getUserByUsername("user1");

        // 验证结果
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());

        // 验证交互
        verify(bankMapper).getUserByUsername("user1");
    }

    @Test
    void testGetUserByUsername_NotExists() {
        // 模拟用户不存在
        when(bankMapper.getUserByUsername("unknown")).thenReturn(null);

        // 调用服务方法
        Bank result = bankService.getUserByUsername("unknown");

        // 验证结果
        assertNull(result);
        verify(bankMapper).getUserByUsername("unknown");
    }

    @Test
    void testAddUser() {
        // 调用服务方法
        bankService.addUser("newUser", BigDecimal.valueOf(500));

        // 验证交互
        verify(bankMapper).insertUser(argThat(bank ->
                "newUser".equals(bank.getUsername()) &&
                        BigDecimal.valueOf(500).compareTo(bank.getBalance()) == 0
        ));
    }

    @Test
    void testAddUser_WithNullBalance() {
        // 调用服务方法
        bankService.addUser("nullBalanceUser", null);

        // 验证交互
        verify(bankMapper).insertUser(argThat(bank ->
                "nullBalanceUser".equals(bank.getUsername()) &&
                        bank.getBalance() == null
        ));
    }

    @Test
    void testAddUser_WithNegativeBalance() {
        // 调用服务方法
        bankService.addUser("negativeBalanceUser", BigDecimal.valueOf(-100));

        // 验证交互
        verify(bankMapper).insertUser(argThat(bank ->
                "negativeBalanceUser".equals(bank.getUsername()) &&
                        BigDecimal.valueOf(-100).compareTo(bank.getBalance()) == 0
        ));
    }

    @Test
    void testUpdateBalance() {
        // 调用服务方法
        bankService.updateBalance("user1", BigDecimal.valueOf(1500));

        // 验证交互
        verify(bankMapper).updateBalance("user1", BigDecimal.valueOf(1500));
    }

    @Test
    void testUpdateBalance_ToNull() {
        // 调用服务方法
        bankService.updateBalance("user1", null);

        // 验证交互
        verify(bankMapper).updateBalance("user1", null);
    }

    @Test
    void testUpdateBalance_ToNegative() {
        // 调用服务方法
        bankService.updateBalance("user1", BigDecimal.valueOf(-500));

        // 验证交互
        verify(bankMapper).updateBalance("user1", BigDecimal.valueOf(-500));
    }

    @Test
    void testUpdateBalance_NonExistentUser() {
        // 模拟用户不存在时的Mapper行为
        doNothing().when(bankMapper).updateBalance(anyString(), any());

        // 调用服务方法
        bankService.updateBalance("nonExistentUser", BigDecimal.valueOf(1000));

        // 验证交互
        verify(bankMapper).updateBalance("nonExistentUser", BigDecimal.valueOf(1000));
    }

    @Test
    void testUpdateBalance_WithExtremeValues() {
        // 测试极大值
        BigDecimal largeValue = new BigDecimal("999999999999999999999999999999.99");
        bankService.updateBalance("user1", largeValue);
        verify(bankMapper).updateBalance("user1", largeValue);

        // 测试极小值
        BigDecimal smallValue = new BigDecimal("-999999999999999999999999999999.99");
        bankService.updateBalance("user1", smallValue);
        verify(bankMapper).updateBalance("user1", smallValue);
    }

    @Test
    void testUpdateBalance_WithZero() {
        // 调用服务方法
        bankService.updateBalance("user1", BigDecimal.ZERO);

        // 验证交互
        verify(bankMapper).updateBalance("user1", BigDecimal.ZERO);
    }

    @Test
    void testUpdateBalance_WithPrecision() {
        // 测试高精度数值
        BigDecimal preciseValue = new BigDecimal("1234.56789");
        bankService.updateBalance("user1", preciseValue);
        verify(bankMapper).updateBalance("user1", preciseValue);
    }

    @Test
    void testConstructorInjection() {
        // 验证构造函数注入
        assertNotNull(bankService);
        assertNotNull(bankMapper);
    }
}
