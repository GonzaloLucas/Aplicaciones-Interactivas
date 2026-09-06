package com.uade.tpo.demo.controllers.image;

import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.controllers.dtoResponses.ImageResponse;
import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.service.ImageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("images")
public class ImagesController {

    @Autowired
    private ImageService imageService;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<ImageResponse> displayImage(@RequestParam("id") long id) throws IOException, SQLException {
        Image image = imageService.viewById(id);
        String encodedString = Base64.getEncoder()
                .encodeToString(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity.ok().body(ImageResponse.builder().file(encodedString).id(id).build());
    }

    /**
     * Devuelve todas las imágenes de portada con paginación.
     * GET /images/portadas?page=0&size=10
     */
    @GetMapping("/portadas")
    public ResponseEntity<Page<ImageResponse>> getPortadas(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws SQLException {

        Page<Image> images = (page == null || size == null)
                ? imageService.getPortadaImages(PageRequest.of(0, Integer.MAX_VALUE))
                : imageService.getPortadaImages(PageRequest.of(page, size));

        Page<ImageResponse> response = images.map(image -> {
            try {
                String encoded = Base64.getEncoder()
                        .encodeToString(image.getImage().getBytes(1, (int) image.getImage().length()));
                return ImageResponse.builder()
                        .id(image.getId())
                        .file(encoded)
                        .esPortada(true)
                        .build();
            } catch (SQLException e) {
                throw new RuntimeException("Error al leer imagen id=" + image.getId(), e);
            }
        });

        return ResponseEntity.ok(response);
    }
}
