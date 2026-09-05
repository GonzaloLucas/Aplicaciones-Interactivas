package com.uade.tpo.demo.controllers.dtoResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCardResponse {
    private Long id;
    private String name;
    private Double price;
    private String portadaBase64;
}
