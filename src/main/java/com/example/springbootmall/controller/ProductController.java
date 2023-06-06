package com.example.springbootmall.controller;

import com.example.springbootmall.constant.ProductCategory;
import com.example.springbootmall.dto.ProductQueryParams;
import com.example.springbootmall.dto.ProductRequest;
import com.example.springbootmall.model.Product;
import com.example.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String productName
    ) {
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setProductName(productName);

        List<Product> productList = productService.getProducts(productQueryParams);

        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

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

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductRequest productRequest) {
        ResponseEntity<Product> result;

        // check product
        Product product = productService.getProductById(productId);

        if (Objects.nonNull(product)) {
            // update product data
            productService.updateProduct(productId, productRequest);

            Product updatedProduct = productService.getProductById(productId);

            result = ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } else {
            result = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return result;
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
