package com.uade.tpo.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.uade.tpo.demo.entity.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long productId);

    Page<Image> findByEsPortadaTrue(Pageable pageable);
}