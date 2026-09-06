package com.uade.tpo.demo.service;

import java.sql.Blob;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.controllers.product.ProductRequest;
import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.repository.CategoryRepository;
import com.uade.tpo.demo.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
    }

    @Override
    @Transactional // Asegura que se guarde el producto y todas sus imágenes en una sola
                   // transacción
    public Product createProduct(ProductRequest request, List<MultipartFile> files) throws Exception {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        // Procesamiento de las imágenes y asignación de portada
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (!file.isEmpty()) {
                    byte[] bytes = file.getBytes();
                    Blob blob = new SerialBlob(bytes);

                    Image img = Image.builder()
                            .image(blob)
                            .esPortada(i == 0) // La primera imagen (índice 0) será la portada
                            .build();

                    product.addImage(img);
                }
            }
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest request, List<MultipartFile> files) throws Exception {
        Product product = getProductById(id);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Categoría no encontrada: " + request.getCategoryId()));
            product.setCategory(category);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        // Procesar nuevas imágenes si se enviaron
        if (files != null && !files.isEmpty()) {
            boolean tieneImagenes = !product.getImages().isEmpty();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (!file.isEmpty()) {
                    byte[] bytes = file.getBytes();
                    Blob blob = new SerialBlob(bytes);

                    Image img = Image.builder()
                            .image(blob)
                            .esPortada(!tieneImagenes && i == 0)
                            .build();

                    product.addImage(img);
                }
            }
        }

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}