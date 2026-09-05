package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.controllers.product.ProductRequest;
import com.uade.tpo.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {
    Page<Product> getAllProducts(PageRequest pageRequest);

    Product getProductById(Long id);

    Product createProduct(ProductRequest request);

    Product updateProduct(Long id, ProductRequest request);

    Product deleteProduct(Long id);

    Product applyDiscount(Long productId, Double discountPercentage);
}
