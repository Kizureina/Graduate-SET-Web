package com.example.setweb.controller;

import com.example.setweb.dao.Product;
import com.example.setweb.mapper.ProductMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author Yoruko
 */
@RestController
@RequestMapping("/api/seller")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Resource
    private ProductMapper productMapper;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        logger.info("返回商家的所有商品");
        return productMapper.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getUserById(@PathVariable int id) {
        logger.info("基于商品" + id + "查询商品");
        return productMapper.getProductById(id);
    }
}
