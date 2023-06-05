package com.example.springbootmall.controller;

import com.example.springbootmall.dto.ProductRequest;
import com.example.springbootmall.model.Product;
import com.example.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
            result = ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            result = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return result;
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Integer productId = productService.createProduct(productRequest);

        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
}
