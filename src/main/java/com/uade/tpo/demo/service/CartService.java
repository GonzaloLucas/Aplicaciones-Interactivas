
package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.CartItem;

public interface CartService {

    public Cart getOrCreateCart(Long userId);

    public Page<CartItem> getCartItems(Long userId, PageRequest pageRequest);

    public CartItem addItem(Long userId, Long productId, int quantity);
    
    public void removeItem(Long userId, Long productId);

    public void updateItemQuantity(Long userId, Long productId, int quantity);

    public void cleanCart(Long userId);

    // operaciones de logica de negoico extra

    public boolean validateCart(Long userId);

    public double calculateTotal(Long userId);

}