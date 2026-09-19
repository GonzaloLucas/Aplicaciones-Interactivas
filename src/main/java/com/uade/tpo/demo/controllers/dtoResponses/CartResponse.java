package com.uade.tpo.demo.controllers.dtoResponses;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {
    private Long id;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemResponse> items;
}
