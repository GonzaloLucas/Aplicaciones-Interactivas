package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Order;
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
    Order createOrder(Double total);
    Page<Order> getOrders(PageRequest pageRequest);
    Optional<Order> getOrderById(Long orderId);
    Order updateOrder(Long orderId, String status);
    Page<Order> getOrdersByUserId(Long userId, PageRequest pageRequest);
}
