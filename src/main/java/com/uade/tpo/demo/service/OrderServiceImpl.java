package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderStatus;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.OrderNotFoundException;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired //inyeccion de orderRepository
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Page<Order> getOrders(PageRequest pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public Order getOrderById(Long orderId) throws OrderNotFoundException{
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        }
        throw new OrderNotFoundException();
    }


    @Transactional
    public Order createOrder(Double total, OrderStatus status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findUserByEmail(email);

        return orderRepository.save(new Order(total, user, status));
    }

    @Transactional
    public Order updateOrder(Long orderId, OrderStatus status, Double total) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order orderToUpdate = order.get();
            orderToUpdate.setStatus(status);
            orderToUpdate.setTotal(total);
            return orderRepository.save(orderToUpdate);
        }
        throw new OrderNotFoundException();
    }

    @Transactional
    public Order deleteOrder(Long orderId) throws OrderNotFoundException{
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            orderRepository.deleteById(orderId);
            return order.get();
        }
        throw new OrderNotFoundException();
    }
}
