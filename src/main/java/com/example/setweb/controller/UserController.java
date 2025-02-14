package com.example.setweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yoruko
 */
@RestController
@RequestMapping("/users")
public class UserController {
        @GetMapping("/{id}")
        public String getById(@PathVariable Integer id) {
            System.out.println("get id ==> " + id);
            return "hello,spring boot!";
        }
}
