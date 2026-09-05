package com.uade.tpo.demo.controllers.image;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.controllers.dtoResponses.ImageResponse;
import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.service.ImageService;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    /**
     * @deprecated Usar POST /products/{id}/images para subir imágenes asociadas a
     *             un producto.
     */
    @Deprecated
    @PostMapping()
    public String addImagesPost(@RequestParam("archivos") List<MultipartFile> archivos)
            throws IOException, SerialException, SQLException {

        // 1. Validamos que realmente hayan llegado archivos
        if (archivos == null || archivos.isEmpty()) {
            return "Error: No se recibieron imágenes.";
        }

        int cantidadGuardadas = 0;

        // 2. Recorremos la lista de archivos uno por uno
        for (MultipartFile archivo : archivos) {
            // Verificamos que el archivo actual no esté vacío
            if (!archivo.isEmpty()) {
                byte[] bytes = archivo.getBytes();
                Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

                // 3. Al guardar en el servicio, la base de datos genera un ID único para esta
                // imagen
                imageService.create(Image.builder().image(blob).build());

                cantidadGuardadas++;
            }
        }

        return "Éxito: Se guardaron " + cantidadGuardadas + " imágenes correctamente.";
    }
}
