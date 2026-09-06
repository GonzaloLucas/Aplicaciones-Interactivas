package com.uade.tpo.demo.controllers.dtoResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCardResponse {
    private Long id;
    private String name;
    private Double discountPercentage;
    private Double price;
    private Double finalPrice;
    private String portadaBase64;
}
