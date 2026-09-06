package com.uade.tpo.demo.controllers.product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.controllers.dtoResponses.CategoryResponse;
import com.uade.tpo.demo.controllers.dtoResponses.ImageResponse;
import com.uade.tpo.demo.controllers.dtoResponses.ProductCardResponse;
import com.uade.tpo.demo.controllers.dtoResponses.ProductDetailResponse;
import com.uade.tpo.demo.entity.Image;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.service.ImageService;
import com.uade.tpo.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;

    // ==================== Endpoints públicos ====================

    @GetMapping
    public ResponseEntity<Page<ProductCardResponse>> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Page<Product> products = (page == null || size == null)
                ? productService.getAllProducts(PageRequest.of(0, Integer.MAX_VALUE))
                : productService.getAllProducts(PageRequest.of(page, size));
        return ResponseEntity.ok(products.map(this::toCardResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(toDetailResponse(productService.getProductById(id)));
    }

    // ==================== Endpoints de administración ====================

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductDetailResponse> createProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) throws Exception {

        Product created = productService.createProduct(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDetailResponse(created));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) throws Exception {
        return ResponseEntity.ok(toDetailResponse(productService.updateProduct(id, request, files)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(toDetailResponse(productService.deleteProduct(id)));
    }

    @PutMapping("/{id}/discount")
    public ResponseEntity<ProductDetailResponse> applyDiscount(@PathVariable Long id, @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(toDetailResponse(productService.applyDiscount(id, request.getDiscountPercentage())));
    }

    // ==================== Endpoints de imágenes (admin) ====================

    /**
     * Sube N imágenes y las asocia a un producto existente.
     * POST /products/{id}/images
     */
    @PostMapping(value = "/{id}/images", consumes = { "multipart/form-data" })
    public ResponseEntity<String> addImagesToProduct(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) throws Exception {
        imageService.addImagesToProduct(id, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Se agregaron " + files.size() + " imágenes al producto " + id);
    }

    /**
     * Elimina una imagen específica de un producto.
     * DELETE /products/{id}/images/{imageId}
     */
    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Helpers de conversión ====================

    /**
     * Convierte Product a ProductCardResponse (tarjeta de listado).
     * Extrae solo la imagen de portada.
     */
    private ProductCardResponse toCardResponse(Product product) {
        String portadaBase64 = null;

        // Buscar la imagen de portada entre las imágenes del producto
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
                .discountPercentage(product.getDiscountPercentage())
                .finalPrice(product.getFinalPrice())
                .portadaBase64(portadaBase64)
                .build();
    }

    /**
     * Convierte Product a ProductDetailResponse (vista de detalle).
     * Incluye todas las imágenes, categoría y todos los campos.
     */
    private ProductDetailResponse toDetailResponse(Product product) {
        // Categoría
        CategoryResponse categoryResponse = null;
        if (product.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .description(product.getCategory().getDescription())
                    .build();
        }

        // Imágenes
        List<ImageResponse> imageResponses = new ArrayList<>();
        if (product.getImages() != null) {
            for (Image img : product.getImages()) {
                if (img.getImage() != null) {
                    try {
                        String encoded = Base64.getEncoder()
                                .encodeToString(img.getImage().getBytes(1, (int) img.getImage().length()));
                        imageResponses.add(ImageResponse.builder()
                                .id(img.getId())
                                .file(encoded)
                                .esPortada(img.isEsPortada())
                                .build());
                    } catch (SQLException e) {
                        // Si falla la lectura del blob, omitimos esta imagen
                    }
                }
            }
        }

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .discountPercentage(product.getDiscountPercentage())
                .finalPrice(product.getFinalPrice())
                .category(categoryResponse)
                .images(imageResponses)
                .build();
    }
}
