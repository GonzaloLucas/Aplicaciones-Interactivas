
package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.CartItem;
import java.util.List;

public interface CartService {

    Cart getOrCreateCart(Long userId);

    List<CartItem> getCartItems(Long userId);

    CartItem getCartItem(Long userId, Long productId);

    CartItem addItem(Long userId, Long productId, int quantity);
    
    void removeItem(Long userId, Long productId);

    CartItem updateItemQuantity(Long userId, Long productId, int quantity);

    void cleanCart(Long userId);

    boolean validateCart(Long userId);

    double calculateTotal(Long userId);

}