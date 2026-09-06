package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.CartItem;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderDetail;
import com.uade.tpo.demo.entity.OrderStatus;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.CartNotFoundException;
import com.uade.tpo.demo.exceptions.EmptyCartException;
import com.uade.tpo.demo.exceptions.OrderNotFoundException;
import com.uade.tpo.demo.exceptions.OutOfStockException;
import com.uade.tpo.demo.repository.CartRepository;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.ProductRepository;
import com.uade.tpo.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired //inyeccion de orderRepository
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired 
    private CartRepository cartRepository;
    @Autowired 
    private ProductRepository productRepository;

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

    @Transactional 
    public Order checkout() throws EmptyCartException, OutOfStockException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findUserByEmail(email);

        Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(CartNotFoundException::new);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        Double total = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new OutOfStockException("Stock insuficiente para \"" + product.getName() + "\". Disponible: " + product.getStock());
            }

            Double unitPrice = product.getFinalPrice();
            Double subtotal = unitPrice * cartItem.getQuantity();
            total += subtotal;

            
            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(cartItem.getQuantity());
            detail.setUnitPrice(unitPrice);
            detail.setSubtotal(subtotal);
            orderDetails.add(detail);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Order order = new Order(total, user, OrderStatus.PAID);
        order.setOrderDetails(orderDetails);

        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
}
