package com.uade.tpo.demo.controllers.dtoResponses;

import java.time.LocalDateTime;
import java.util.List;

import com.uade.tpo.demo.entity.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private Double total;
    private LocalDateTime createdAt;
    private String userName; // nombre y apellido del dueño de la orden, sin exponer el resto del User
    private List<OrderDetailResponse> details;
}
