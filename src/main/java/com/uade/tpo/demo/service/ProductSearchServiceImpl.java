package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    public Page<Product> getAdvancedSearch(String name, Long categoryId, Double minPrice, Double maxPrice,
            String sortType, int page, int size) {

        Sort sort = Sort.unsorted();

        // Traducimos el string del frontend a instrucciones de SQL (ORDER BY)
        if ("price_asc".equals(sortType)) {
            sort = Sort.by("price").ascending();
        } else if ("price_desc".equals(sortType)) {
            sort = Sort.by("price").descending();
        } else if ("name_asc".equals(sortType)) {
            sort = Sort.by("name").ascending();
        }

        // Creamos la paginación inyectándole las reglas de ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAdvanced(name, categoryId, minPrice, maxPrice, pageable);
    }
}
