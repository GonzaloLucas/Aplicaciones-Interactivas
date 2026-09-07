package com.uade.tpo.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1")
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price <= ?1")
    Page<Product> findByPriceLessThanEqual(Double price, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price >= ?1")
    Page<Product> findByPriceGreaterThanEqual(Double price, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price >= ?1 AND p.price <= ?2")
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
