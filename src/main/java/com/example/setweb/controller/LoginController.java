package com.example.setweb.controller;

import com.example.setweb.dao.User;
import com.example.setweb.utils.DESUtil;
import com.example.setweb.utils.X509Generator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yoruko
 */
@RestController
// 统一 API 前缀
@RequestMapping("/api")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // 模拟用户数据库
    public static final Map<String, String> USER_DB = new HashMap<>();
    public static String userNameLogin = "";

    static {
        USER_DB.put("admin", "123456");
        USER_DB.put("user", "123456");
        USER_DB.put("Alice", "123456");
        USER_DB.put("Bob", "123456");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData, HttpSession session) throws Exception {
        // 此处接收的是前端的JSON数据，并赋值给Map对象【而不是表单】
        Map<String, Object> response = new HashMap<>(64);

        String username = loginData.get("username");
        String password = loginData.get("password");

        if (USER_DB.containsKey(username) && USER_DB.get(username).equals(password)) {
            response.put("status", "success");
            response.put("message", "登录成功");
            User loginUser = new User(username, password);

            // 生成用户RSA公钥私钥对
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // 使用Session存储登录信息
            session.setAttribute("user", loginUser.toString());
            session.setAttribute("keyPair", keyPair);
            session.setAttribute("key", DESUtil.generateKey());

            // 用户登录时生成签名和证书
            session.setAttribute("cert",  X509Generator.generator(username, keyPair));

            userNameLogin = loginUser.getUsername();
            logger.info(username + "登录成功，且成功生成RSA密钥对和X509v3证书");

        } else {
            response.put("status", "error");
            response.put("message", "用户名或密码错误，请重试");
            logger.error(username + "登录失败！");
        }

        return response;
    }

    @GetMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws Exception {
        session.invalidate();
        // 清除登录状态
        response.sendRedirect("/login.html");
        // 直接重定向到登录页
    }
}

