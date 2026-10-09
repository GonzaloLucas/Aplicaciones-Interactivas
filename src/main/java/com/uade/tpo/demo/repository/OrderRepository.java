package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    void deleteById(Long id);

    @Query("select o from Order o where o.id = ?1 and o.user.id = ?2")
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // Pone en null la referencia al usuario en todas sus órdenes, sin borrarlas
    // (conserva el historial de ventas). Se usa antes de eliminar un usuario.
    @Modifying
    @Query("UPDATE Order o SET o.user = null WHERE o.user.id = :userId")
    void detachUser(@Param("userId") Long userId);
}
