package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.controllers.product.ProductRequest;
import com.uade.tpo.demo.entity.Product;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product createProduct(ProductRequest request);

    Product updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
