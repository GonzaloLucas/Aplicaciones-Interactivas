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
import com.uade.tpo.demo.exceptions.ImageNotFoundException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.repository.ImageRepository;
import com.uade.tpo.demo.repository.ProductRepository;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Image viewById(long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada: " + id));
    }

    @Override
    @Transactional
    public void addImagesToProduct(Long productId, List<MultipartFile> files) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException());

        boolean tienePortada = product.getImages().stream()
                .anyMatch(Image::isEsPortada);

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    byte[] bytes = file.getBytes();
                    Blob blob = new SerialBlob(bytes);

                    Image image = Image.builder()
                            .image(blob)
                            .esPortada(!tienePortada)
                            .build();

                    product.addImage(image);
                    tienePortada = true;
                }
            }
        }

        productRepository.save(product); // Cascade persiste las imágenes
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada: " + imageId));
        Product product = image.getProduct();
        boolean eraPortada = image.isEsPortada();

        product.getImages().remove(image);
        imageRepository.delete(image);

        if (eraPortada && !product.getImages().isEmpty()) {
            Image nuevaPortada = product.getImages().get(0);
            nuevaPortada.setEsPortada(true);
            imageRepository.save(nuevaPortada);
        }
    }

    @Override
    @Transactional
    public Image replaceImage(Long imageId, MultipartFile file) throws Exception {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada: " + imageId));

        byte[] bytes = file.getBytes();
        Blob blob = new SerialBlob(bytes);
        image.setImage(blob);

        return imageRepository.save(image);
    }

    @Override
    public Page<Image> getPortadaImages(Pageable pageable) {
        return imageRepository.findByEsPortadaTrue(pageable);
    }
}
