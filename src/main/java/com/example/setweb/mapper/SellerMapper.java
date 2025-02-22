package com.example.setweb.mapper;

import com.example.setweb.dao.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Yoruko
 */
@Mapper
public interface SellerMapper {

    @Select("SELECT * FROM products")
    List<Product> getAllProducts();

    @Select("SELECT * FROM products WHERE product_id = #{productId}")
    Product getProductById(int productId);

    @Insert("INSERT INTO products (product_name) VALUES (#{productName})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    void insertProduct(Product product);
}

