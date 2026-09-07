package com.uade.tpo.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.entity.Image;

@Service
public interface ImageService {

    public Image viewById(long id);

    public void addImagesToProduct(Long productId, List<MultipartFile> files) throws Exception;

    public void deleteImage(Long imageId);

    public Page<Image> getPortadaImages(Pageable pageable);
}