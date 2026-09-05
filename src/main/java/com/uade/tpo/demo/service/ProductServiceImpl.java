package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.controllers.product.ProductRequest;
import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;
import com.uade.tpo.demo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Page<Product> getAllProducts(PageRequest pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional
    public Product getProductById(Long id) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFoundException();
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if (category.isPresent()) {
            return productRepository.save(new Product(
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getStock(),
                    request.getImage(),
                    category.get()
            ));
        }
        throw new CategoryNotFoundException();
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product productToUpdate = product.get();

            if (request.getCategoryId() != null) {
                Optional<Category> category = categoryRepository.findById(request.getCategoryId());
                if (category.isPresent()) {
                    productToUpdate.setCategory(category.get());
                } else {
                    throw new CategoryNotFoundException();
                }
            }

            productToUpdate.setName(request.getName());
            productToUpdate.setDescription(request.getDescription());
            productToUpdate.setPrice(request.getPrice());
            productToUpdate.setStock(request.getStock());
            productToUpdate.setImage(request.getImage());

            return productRepository.save(productToUpdate);
        }
        throw new ProductNotFoundException();
    }

    @Transactional
    public Product deleteProduct(Long id) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.deleteById(id);
            return product.get();
        }
        throw new ProductNotFoundException();
    }

    @Transactional
    public Product applyDiscount(Long productId, Double discountPercentage) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            if (discountPercentage == null ||
                discountPercentage < 0 ||
                discountPercentage > 100) {
                throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
            }

            Product productToUpdate = product.get();
            productToUpdate.setDiscountPercentage(discountPercentage);
            return productRepository.save(productToUpdate);
        }
        throw new ProductNotFoundException();
    }
}
