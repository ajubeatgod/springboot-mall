package com.example.springbootmall.dto;

import javax.validation.constraints.NotBlank;

public class BuyItem {

    @NotBlank
    private Integer productId;

    @NotBlank
    private Integer quantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
