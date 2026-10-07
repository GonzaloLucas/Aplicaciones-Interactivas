package com.uade.tpo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Pone en null la referencia al producto en todas las líneas de orden que lo tengan,
    // sin borrar esas líneas (conserva quantity/unitPrice/subtotal como registro histórico).
    // Se usa antes de eliminar un producto del catálogo (ver ProductServiceImpl.deleteProduct).
    @Modifying
    @Query("UPDATE OrderDetail o SET o.product = null WHERE o.product.id = :productId")
    void detachProduct(@Param("productId") Long productId);
}
