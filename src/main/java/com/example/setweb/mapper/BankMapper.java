package com.example.setweb.mapper;

import com.example.setweb.dao.Bank;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yoruko
 */

@Mapper
public interface BankMapper {

    @Select("SELECT * FROM bank_users")
    List<Bank> getAllUsers();

    @Select("SELECT * FROM bank_users WHERE username = #{username}")
    Bank getUserByUsername(String username);

    @Insert("INSERT INTO bank_users (username, balance) VALUES (#{username}, #{balance})")
    void insertUser(Bank user);

    @Update("UPDATE bank_users SET balance = #{balance} WHERE username = #{username}")
    void updateBalance(@Param("username") String username, @Param("balance") BigDecimal balance);

    void deleteUser(String testUsername);
}
