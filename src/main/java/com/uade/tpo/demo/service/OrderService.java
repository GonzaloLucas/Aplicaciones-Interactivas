package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderStatus;
import com.uade.tpo.demo.exceptions.OrderDuplicateException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Double total, OrderStatus status) throws OrderDuplicateException;
    Page<Order> getOrders(PageRequest pageRequest);
    Optional<Order> getOrderById(Long orderId);
    Order updateOrder(Long orderId, OrderStatus status, Double total);
    void deleteOrder(Long orderId);
}
