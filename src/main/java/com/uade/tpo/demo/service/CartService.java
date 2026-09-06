
package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.CartItem;

public interface CartService {

    Cart getOrCreateCart(Long userId);

    Page<CartItem> getCartItems(Long userId, PageRequest pageRequest);

    CartItem addItem(Long userId, Long productId, int quantity);
    
    void removeItem(Long userId, Long productId);

    CartItem updateItemQuantity(Long userId, Long productId, int quantity);

    void cleanCart(Long userId);

    // operaciones de logica de negoico extra

    boolean validateCart(Long userId);

    double calculateTotal(Long userId);

}