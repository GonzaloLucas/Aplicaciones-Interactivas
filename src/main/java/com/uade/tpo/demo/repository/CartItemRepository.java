package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Page<CartItem> findByCart_User_Id(Long userId, Pageable pageable);

    Optional<CartItem> findByCart_User_IdAndProduct_Id(Long userId, Long productId);

    void deleteByCart_User_IdAndProduct_Id(Long userId, Long productId);

    void deleteByCart_Id(Long cartId);
}
