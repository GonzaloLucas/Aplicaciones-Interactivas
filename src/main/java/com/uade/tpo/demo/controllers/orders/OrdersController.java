package com.uade.tpo.demo.controllers.orders;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.exceptions.OrderNotFoundException;
import com.uade.tpo.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<Order>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders(PageRequest.of(0, Integer.MAX_VALUE)));
    }

    @GetMapping("/{OrderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long OrderId)
    throws OrderNotFoundException {
        Order result = orderService.getOrderById(OrderId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrdersRequest ordersRequest) {
        Order result = orderService.createOrder(ordersRequest.getTotal(), ordersRequest.getStatus());
        return ResponseEntity.created(URI.create("/orders/" + result.getId())).body(result);
    }

    @PutMapping("/{OrderId}")
    public ResponseEntity<Object> updateOrder(@RequestBody OrdersRequest ordersRequest, @PathVariable Long OrderId) throws OrderNotFoundException {
        Order result = orderService.updateOrder(OrderId, ordersRequest.getStatus(), ordersRequest.getTotal());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{OrderId}")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long OrderId) throws OrderNotFoundException{
        Order result = orderService.deleteOrder(OrderId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout() {
        Order result = orderService.checkout();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}