package com.example.setweb.controller;

import com.example.setweb.dao.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

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
    private static final Map<String, String> USER_DB = new HashMap<>();

    static {
        USER_DB.put("admin", "123456");
        USER_DB.put("user", "password");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        // 此处接收的是前端的JSON数据，并赋值给Map对象【而不是表单】
        Map<String, Object> response = new HashMap<>(64);

        String username = loginData.get("username");
        String password = loginData.get("password");

        if (USER_DB.containsKey(username) && USER_DB.get(username).equals(password)) {
            response.put("status", "success");
            response.put("message", "登录成功");
            User loginUser = new User(username, password);
            // 使用Session存储登录信息
            session.setAttribute("user", loginUser.toString());
            logger.info(username + "登录成功");
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

