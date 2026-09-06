package com.uade.tpo.demo.controllers.dtoResponses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Double discountPercentage;
    private Double finalPrice;
    private CategoryResponse category;
    private List<ImageResponse> images;
}
