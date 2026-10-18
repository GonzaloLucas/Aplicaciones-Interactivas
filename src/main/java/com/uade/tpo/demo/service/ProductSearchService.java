package com.uade.tpo.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.Product;

public interface ProductSearchService {

    public Page<Product> getProductsByCategory(Long categoryId, PageRequest pageRequest);

    public Page<Product> getProductsByMaxPrice(Double maxPrice, PageRequest pageRequest);

    public Page<Product> getProductsByMinPrice(Double minPrice, PageRequest pageRequest);

    public Page<Product> getProductsByPriceRange(Double minPrice, Double maxPrice, PageRequest pageRequest);

    public Page<Product> getProductsByName(String name, PageRequest pageRequest);

    Page<Product> getAdvancedSearch(String name, Long categoryId, Double minPrice, Double maxPrice, String sortType,
            int page, int size);
}
