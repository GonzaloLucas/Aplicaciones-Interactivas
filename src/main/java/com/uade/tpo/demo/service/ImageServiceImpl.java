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

import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.repository.ImageRepository;
import com.uade.tpo.demo.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Image viewById(long id) {
        return imageRepository.findById(id).get();
    }

    @Override
    @Transactional
    public void addImagesToProduct(Long productId, List<MultipartFile> files) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + productId));

        // Si el producto no tiene imágenes, la primera subida será la portada
        boolean tieneImagenes = !product.getImages().isEmpty();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                byte[] bytes = file.getBytes();
                Blob blob = new SerialBlob(bytes);

                Image img = Image.builder()
                        .image(blob)
                        .esPortada(!tieneImagenes && i == 0) // Portada solo si no tenía imágenes previas
                        .build();

                product.addImage(img); // Vincula ambos lados de la relación
            }
        }

        productRepository.save(product); // Cascade persiste las imágenes
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Imagen no encontrada: " + imageId));
        imageRepository.delete(image);
    }

    @Override
    public Page<Image> getPortadaImages(Pageable pageable) {
        return imageRepository.findByEsPortadaTrue(pageable);
    }
}
