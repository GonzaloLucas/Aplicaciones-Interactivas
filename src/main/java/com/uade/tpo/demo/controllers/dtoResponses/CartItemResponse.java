package com.uade.tpo.demo.controllers.dtoResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long productId;
    private String name;
    private Double price;      // precio final del producto (con descuento aplicado, si tiene)
    private Integer quantity;
    private Double subtotal;   // price * quantity
    private String portadaBase64;
}
