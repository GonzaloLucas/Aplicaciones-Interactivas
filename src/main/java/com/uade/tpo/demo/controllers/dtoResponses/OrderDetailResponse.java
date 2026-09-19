package com.uade.tpo.demo.controllers.dtoResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailResponse {
    private String productName; // "Producto eliminado" si el producto original ya no existe
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
}
