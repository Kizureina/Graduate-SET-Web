package com.example.setweb.controller;

import com.example.setweb.model.CartItem;
import com.example.setweb.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestBody CartItem item, HttpSession session) {
        cartService.addToCart(session, item);
        return "添加成功";
    }

    @GetMapping
    public List<CartItem> getCart(HttpSession session) {
        return cartService.getCart(session);
    }
}
