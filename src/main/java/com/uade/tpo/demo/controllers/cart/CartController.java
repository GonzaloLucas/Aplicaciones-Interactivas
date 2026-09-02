package com.uade.tpo.demo.controllers.cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.CartItem;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Page<CartItem>> getCartItems(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        PageRequest pageRequest = (page == null || size == null)
                ? PageRequest.of(0, Integer.MAX_VALUE)
                : PageRequest.of(page, size);
        return ResponseEntity.ok(cartService.getCartItems(user.getId(), pageRequest));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotal(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.calculateTotal(user.getId()));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.validateCart(user.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addItem(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest request) {
        CartItem item = cartService.addItem(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> updateItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @RequestBody CartItemRequest request) {
        cartService.updateItemQuantity(user.getId(), productId, request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        cartService.removeItem(user.getId(), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cleanCart(@AuthenticationPrincipal User user) {
        cartService.cleanCart(user.getId());
        return ResponseEntity.noContent().build();
    }
}
