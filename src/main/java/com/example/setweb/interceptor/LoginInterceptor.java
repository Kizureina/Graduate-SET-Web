package com.example.setweb.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Yoruko
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String uri = request.getRequestURI();

        // 放行登录页、API请求、静态资源
        if (uri.contains("login") || uri.contains("css") || uri.contains("js") || uri.contains("img") || uri.contains("/api/login")) {
            return true;
        }

        // 其他请求必须有 session
        if (session.getAttribute("user") == null) {
            response.sendRedirect("/login.html"); // 未登录重定向
            return false;
        }

        return true; // 放行
    }
}
