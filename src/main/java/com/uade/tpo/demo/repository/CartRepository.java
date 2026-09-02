package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Un usuario tiene un solo carrito (OneToOne), por eso Optional en vez de List
    @Query("select c from Cart c where c.user.id = ?1")
    Optional<Cart> findByUserId(Long userId);

}
