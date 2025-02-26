package com.example.setweb.service;

import com.example.setweb.dao.Product;
import com.example.setweb.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yoruko
 */
@Service
public class ProductService {
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> getAllUsers() {
        return productMapper.getAllProducts();
    }

    public Product getUserByUsername(int id) {
        return productMapper.getProductById(id);
    }

    public Product insertProduct(int id, String productName){
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(productName);
        productMapper.insertProduct(product);
        return product;
    }

}
