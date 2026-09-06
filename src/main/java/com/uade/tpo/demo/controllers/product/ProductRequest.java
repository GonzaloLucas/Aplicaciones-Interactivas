package com.uade.tpo.demo.controllers.product;

import com.uade.tpo.demo.entity.Image;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long categoryId;
}
