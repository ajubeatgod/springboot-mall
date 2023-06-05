package com.example.springbootmall.controller;

import com.example.springbootmall.model.Product;
import com.example.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {
        ResponseEntity<Product> result;

        Product product = productService.getProductById(productId);

        if (Objects.nonNull(product)) {
            result = ResponseEntity.ok().body(product);
        } else {
            result = ResponseEntity.notFound().build();
        }

        return result;
    }
}
