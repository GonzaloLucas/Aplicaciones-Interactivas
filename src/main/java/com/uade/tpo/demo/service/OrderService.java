package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface OrderService {
    Order createOrder(Double total, OrderStatus status);
    Page<Order> getOrders(PageRequest pageRequest);
    Order getOrderById(Long orderId);
    Order updateOrder(Long orderId, OrderStatus status, Double total);
    Order deleteOrder(Long orderId);
}
