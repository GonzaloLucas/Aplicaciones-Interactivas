package com.uade.tpo.demo.controllers.orders;

import com.uade.tpo.demo.controllers.dtoResponses.OrderDetailResponse;
import com.uade.tpo.demo.controllers.dtoResponses.OrderResponse;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderDetail;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.EmptyCartException;
import com.uade.tpo.demo.exceptions.OrderNotFoundException;
import com.uade.tpo.demo.exceptions.OutOfStockException;
import com.uade.tpo.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders() {
        Page<Order> orders = orderService.getOrders(PageRequest.of(0, Integer.MAX_VALUE));
        return ResponseEntity.ok(orders.map(this::toResponse));
    }

    @GetMapping("/{OrderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long OrderId)
            throws OrderNotFoundException {
        Order result = orderService.getOrderById(OrderId);
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrdersRequest ordersRequest) {
        Order result = orderService.createOrder(ordersRequest.getTotal(), ordersRequest.getStatus());
        return ResponseEntity.created(URI.create("/orders/" + result.getId())).body(toResponse(result));
    }

    @PutMapping("/{OrderId}")
    public ResponseEntity<OrderResponse> updateOrder(@RequestBody OrdersRequest ordersRequest, @PathVariable Long OrderId)
            throws OrderNotFoundException {
        Order result = orderService.updateOrder(OrderId, ordersRequest.getStatus(), ordersRequest.getTotal());
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{OrderId}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable Long OrderId) throws OrderNotFoundException {
        Order result = orderService.deleteOrder(OrderId);
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() throws EmptyCartException, OutOfStockException {
        Order result = orderService.checkout();
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PostMapping("/pay/{OrderId}")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long OrderId) throws OrderNotFoundException {
        Order result = orderService.payOrder(OrderId);
        return ResponseEntity.ok(toResponse(result));
    }

    // ==================== Helpers de conversión ====================

    private OrderResponse toResponse(Order order) {
        User user = order.getUser();
        String userName = user != null
                ? ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                        + (user.getLastName() != null ? user.getLastName() : "")).trim()
                : null;

        List<OrderDetailResponse> details = order.getOrderDetails() == null
                ? new ArrayList<>()
                : order.getOrderDetails().stream().map(this::toDetailResponse).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .userName(userName)
                .details(details)
                .build();
    }

    private OrderDetailResponse toDetailResponse(OrderDetail detail) {
        Product product = detail.getProduct();
        return OrderDetailResponse.builder()
                .productName(product != null ? product.getName() : "Producto eliminado")
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .subtotal(detail.getSubtotal())
                .build();
    }
}