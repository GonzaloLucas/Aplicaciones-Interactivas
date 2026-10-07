package com.uade.tpo.demo.controllers.cart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.uade.tpo.demo.controllers.dtoResponses.CartItemResponse;
import com.uade.tpo.demo.controllers.dtoResponses.CartResponse;
import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toCartResponse(cartService.getOrCreateCart(user.getId())));
    }

    @GetMapping("/items")
    public ResponseEntity<Page<CartItemResponse>> getCartItems(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        PageRequest pageRequest = (page == null || size == null)
                ? PageRequest.of(0, Integer.MAX_VALUE)
                : PageRequest.of(page, size);
        Page<CartItem> items = cartService.getCartItems(user.getId(), pageRequest);
        return ResponseEntity.ok(items.map(this::toItemResponse));
    }

    @GetMapping("/items/{productId}")
    public ResponseEntity<CartItemResponse> getCartItem(@AuthenticationPrincipal User user, @PathVariable Long productId) {
        return ResponseEntity.ok(toItemResponse(cartService.getCartItem(user.getId(), productId)));
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
    public ResponseEntity<CartItemResponse> addItem(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest request) {
        CartItem item = cartService.addItem(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(toItemResponse(item));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartItemResponse> updateItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @RequestBody CartItemUpdateRequest request) {

        CartItem item = cartService.updateItemQuantity(user.getId(), productId, request.getQuantity());
        if (item != null) {
            return ResponseEntity.ok(toItemResponse(item));
        }
        return ResponseEntity.noContent().build();
    }

    // Elimina un producto puntual del carrito y confirma con un mensaje
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Map<String, String>> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        cartService.removeItem(user.getId(), productId);
        return ResponseEntity.ok(Collections.singletonMap("message",
                "El producto " + productId + " fue eliminado del carrito correctamente"));
    }

    // Vacía el carrito completo y confirma con un mensaje
    @DeleteMapping
    public ResponseEntity<Map<String, String>> cleanCart(@AuthenticationPrincipal User user) {
        cartService.cleanCart(user.getId());
        return ResponseEntity.ok(Collections.singletonMap("message",
                "El carrito fue vaciado correctamente"));
    }

    // ==================== Helpers de conversión ====================

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems() == null
                ? new ArrayList<>()
                : cart.getItems().stream().map(this::toItemResponse).collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .status(cart.getStatus())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .items(items)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        Double finalPrice = product != null && product.getFinalPrice() != null ? product.getFinalPrice() : 0.0;
        Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;

        String portadaBase64 = null;
        if (product != null && product.getImages() != null) {
            for (Image img : product.getImages()) {
                if (img.isEsPortada() && img.getImage() != null) {
                    try {
                        portadaBase64 = Base64.getEncoder()
                                .encodeToString(img.getImage().getBytes(1, (int) img.getImage().length()));
                    } catch (SQLException e) {
                        // Si falla la lectura del blob, dejamos portada como null
                    }
                    break;
                }
            }
        }

        return CartItemResponse.builder()
                .productId(product != null ? product.getId() : null)
                .name(product != null ? product.getName() : null)
                .price(finalPrice)
                .quantity(quantity)
                .subtotal(finalPrice * quantity)
                .portadaBase64(portadaBase64)
                .build();
    }
}
