package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.repository.ProductRepository;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getProductsByCategory(Long categoryId, PageRequest pageRequest) {
        return productRepository.findByCategoryId(categoryId, pageRequest);
    }

    @Override
    public Page<Product> getProductsByMaxPrice(Double maxPrice, PageRequest pageRequest) {
        return productRepository.findByPriceLessThanEqual(maxPrice, pageRequest);
    }

    @Override
    public Page<Product> getProductsByMinPrice(Double minPrice, PageRequest pageRequest) {
        return productRepository.findByPriceGreaterThanEqual(minPrice, pageRequest);
    }

    @Override
    public Page<Product> getProductsByPriceRange(Double minPrice, Double maxPrice, PageRequest pageRequest) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageRequest);
    }

    @Override
    public Page<Product> getProductsByName(String name, PageRequest pageRequest) {
        return productRepository.findByNameContainingIgnoreCase(name, pageRequest);
    }
}
