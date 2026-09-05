package com.uade.tpo.demo.controllers.orders;

import com.uade.tpo.demo.entity.OrderStatus;
import lombok.Data;

@Data
public class OrdersRequest {
    private Double total;
    private OrderStatus status;
}
