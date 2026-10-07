package com.uade.tpo.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.CartItem;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.exceptions.CartItemNotFoundException;
import com.uade.tpo.demo.exceptions.OutOfStockException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.exceptions.UserNotFoundException;
import com.uade.tpo.demo.repository.CartItemRepository;
import com.uade.tpo.demo.repository.CartRepository;
import com.uade.tpo.demo.repository.ProductRepository;
import com.uade.tpo.demo.repository.UserRepository;

@Service
public class CartServiceImple implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Devuelve el carrito del usuario. Si todavia no tiene uno (primera compra),
     * lo crea.
     */
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId)));
                    cart.setStatus("ACTIVE");
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setUpdatedAt(LocalDateTime.now());
                    cart.setItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    @Override
    public CartItem getCartItem(Long userId, Long productId) {
        CartItem item = cartItemRepository.findByCart_User_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new CartItemNotFoundException(
                        "El producto " + productId + " no esta en el carrito del usuario " + userId));
        return item;
    }

    @Override
    public Page<CartItem> getCartItems(Long userId, PageRequest pageRequest) {
        return cartItemRepository.findByCart_User_Id(userId, pageRequest);
    }

    @Override
    @Transactional
    public CartItem addItem(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException());

        // Si el producto ya esta en el carrito, sumamos en canitdad en vez de tener 2 productos
        CartItem item = cartItemRepository.findByCart_User_IdAndProduct_Id(userId, productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newQuantity = item.getQuantity() + quantity;
        if (product.getStock() != null && newQuantity > product.getStock()) {
            throw new OutOfStockException(
                    "Stock insuficiente para \"" + product.getName() + "\". Disponible: " + product.getStock());
        }

        item.setQuantity(newQuantity);
        CartItem saved = cartItemRepository.save(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return saved;
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long productId) {
        CartItem item = cartItemRepository.findByCart_User_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new CartItemNotFoundException(
                        "El producto " + productId + " no esta en el carrito del usuario " + userId));

        cartItemRepository.delete(item);
        touchCart(userId);
    }

    @Override
    @Transactional
    public CartItem updateItemQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            // Actualizar a 0 (o menos) equivale a sacar el producto del carrito
            removeItem(userId, productId);
            return null;
        }

        CartItem item = cartItemRepository.findByCart_User_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new CartItemNotFoundException(
                        "El producto " + productId + " no esta en el carrito del usuario " + userId));

        Product product = item.getProduct();
        if (product.getStock() != null && quantity > product.getStock()) {
            throw new OutOfStockException(
                    "Stock insuficiente para \"" + product.getName() + "\". Disponible: " + product.getStock());
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        touchCart(userId);
        return item;
    }

    @Override
    @Transactional
    public void cleanCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart_Id(cart.getId());
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public boolean validateCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return false;
        }
        return cart.getItems().stream().allMatch(item -> {
            Product product = item.getProduct();
            return product != null
                    && item.getQuantity() != null
                    && item.getQuantity() > 0
                    && product.getStock() != null
                    && item.getQuantity() <= product.getStock();
        });
    }

    // Usa getFinalPrice() (precio con descuento aplicado si corresponde) para que el total
    // del carrito coincida con lo que después va a cobrar el checkout.
    @Override
    public double calculateTotal(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems() == null) {
            return 0.0;
        }
        return cart.getItems().stream()
                .mapToDouble(item -> {
                    double finalPrice = item.getProduct() != null && item.getProduct().getFinalPrice() != null
                            ? item.getProduct().getFinalPrice()
                            : 0.0;
                    return finalPrice * item.getQuantity();
                })
                .sum();
    }

    @Transactional
    private void touchCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}