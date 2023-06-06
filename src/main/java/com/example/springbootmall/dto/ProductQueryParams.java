package com.example.springbootmall.dto;

import com.example.springbootmall.constant.ProductCategory;

public class ProductQueryParams {

    private ProductCategory category;
    private String productName;

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
