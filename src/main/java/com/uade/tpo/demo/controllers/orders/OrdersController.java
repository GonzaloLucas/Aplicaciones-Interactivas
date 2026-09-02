package com.uade.tpo.demo.controllers.orders;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.service.CategoryService;
import com.uade.tpo.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Optional<Order> result = orderService.getOrderById(orderId);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrdersRequest ordersRequest)
            throws CategoryDuplicateException {
        Order result = orderService.createOrder((Double) ordersRequest.getTotal());
        return ResponseEntity.created(URI.create("/orders/" + result.getId())).body(result);
    }
}
