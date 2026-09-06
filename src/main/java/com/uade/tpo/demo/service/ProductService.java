package com.uade.tpo.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.controllers.product.ProductRequest;
import com.uade.tpo.demo.entity.Product;

public interface ProductService {

    Page<Product> getAllProducts(Pageable pageable);

    Product getProductById(Long id);

    Product createProduct(ProductRequest request, List<MultipartFile> files) throws Exception;

    Product updateProduct(Long id, ProductRequest request, List<MultipartFile> files) throws Exception;

    void deleteProduct(Long id);
}
