package com.example.setweb.service;

import com.example.setweb.model.CartItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yoruko
 */
@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";

    public void addToCart(HttpSession session, CartItem item) {
        List<CartItem> cart = getCart(session);
        for (CartItem existing : cart) {
            if (existing.getProductId().equals(item.getProductId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                session.setAttribute(CART_SESSION_KEY, cart);
                return;
            }
        }
        cart.add(item);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
}
