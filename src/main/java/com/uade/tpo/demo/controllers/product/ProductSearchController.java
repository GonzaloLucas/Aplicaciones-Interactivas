package com.uade.tpo.demo.controllers.product;

import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.controllers.dtoResponses.ProductCardResponse;
import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.service.ProductSearchService;

@RestController
@RequestMapping("/products/search")
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    // ==================== Filtro por categoría ====================

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<Product> products = (page == null || size == null)
                ? productSearchService.getProductsByCategory(categoryId, PageRequest.of(0, Integer.MAX_VALUE))
                : productSearchService.getProductsByCategory(categoryId, PageRequest.of(page, size));

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message",
                    "No se encontraron productos en la categoría seleccionada"));
        }

        return ResponseEntity.ok(products.map(this::toCardResponse));
    }

    // ==================== Filtro por precio máximo ====================

    @GetMapping("/max-price")
    public ResponseEntity<?> getProductsByMaxPrice(
            @RequestParam Double maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<Product> products = (page == null || size == null)
                ? productSearchService.getProductsByMaxPrice(maxPrice, PageRequest.of(0, Integer.MAX_VALUE))
                : productSearchService.getProductsByMaxPrice(maxPrice, PageRequest.of(page, size));

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message",
                    "No se encontraron productos con un precio menor o igual a " + maxPrice));
        }

        return ResponseEntity.ok(products.map(this::toCardResponse));
    }

    // ==================== Filtro por precio mínimo ====================

    @GetMapping("/min-price")
    public ResponseEntity<?> getProductsByMinPrice(
            @RequestParam Double minPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<Product> products = (page == null || size == null)
                ? productSearchService.getProductsByMinPrice(minPrice, PageRequest.of(0, Integer.MAX_VALUE))
                : productSearchService.getProductsByMinPrice(minPrice, PageRequest.of(page, size));

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message",
                    "No se encontraron productos con un precio mayor o igual a " + minPrice));
        }

        return ResponseEntity.ok(products.map(this::toCardResponse));
    }

    // ==================== Filtro por rango de precios ====================

    @GetMapping("/price-range")
    public ResponseEntity<?> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<Product> products = (page == null || size == null)
                ? productSearchService.getProductsByPriceRange(minPrice, maxPrice, PageRequest.of(0, Integer.MAX_VALUE))
                : productSearchService.getProductsByPriceRange(minPrice, maxPrice, PageRequest.of(page, size));

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message",
                    "No se encontraron productos con un precio entre " + minPrice + " y " + maxPrice));
        }

        return ResponseEntity.ok(products.map(this::toCardResponse));
    }

    // ==================== Filtro por nombre ====================

    @GetMapping("/name")
    public ResponseEntity<?> getProductsByName(
            @RequestParam String name,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<Product> products = (page == null || size == null)
                ? productSearchService.getProductsByName(name, PageRequest.of(0, Integer.MAX_VALUE))
                : productSearchService.getProductsByName(name, PageRequest.of(page, size));

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message",
                    "No se encontraron productos con el nombre \"" + name + "\""));
        }

        return ResponseEntity.ok(products.map(this::toCardResponse));
    }

    // ==================== Helper de conversión ====================

    /**
     * Convierte Product a ProductCardResponse (tarjeta de listado).
     * Extrae solo la imagen de portada.
     */
    private ProductCardResponse toCardResponse(Product product) {
        String portadaBase64 = null;

        if (product.getImages() != null) {
            for (Image img : product.getImages()) {
                if (img.isEsPortada() && img.getImage() != null) {
                    try {
                        portadaBase64 = Base64.getEncoder()
                                .encodeToString(img.getImage().getBytes(1, (int) img.getImage().length()));
                    } catch (SQLException e) {
                        // Si falla la lectura del blob, dejamos portada como null
                    }
                    break;
                }
            }
        }

        return ProductCardResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .portadaBase64(portadaBase64)
                .build();
    }
}
