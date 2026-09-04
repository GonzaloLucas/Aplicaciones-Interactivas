package com.uade.tpo.demo.service;

import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Image;

@Service
public interface ImageService {
    public Image create(Image image);

    public Image viewById(long id);
}