package com.example.setweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.setweb.controller.LoginController.userNameLogin;

/**
 * @author Yoruko
 */
@RestController
@RequestMapping("/api")
public class UserController {
        @GetMapping("/username")
        public String getMyUserNmae() {
            return userNameLogin;
        }
}
