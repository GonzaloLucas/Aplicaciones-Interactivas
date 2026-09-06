package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderStatus;
import com.uade.tpo.demo.exceptions.EmptyCartException;
import com.uade.tpo.demo.exceptions.OutOfStockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface OrderService {
    Order createOrder(Double total, OrderStatus status);
    Page<Order> getOrders(PageRequest pageRequest);
    Order getOrderById(Long orderId);
    Order updateOrder(Long orderId, OrderStatus status, Double total);
    Order deleteOrder(Long orderId);
    Order checkout() throws EmptyCartException, OutOfStockException;
}
