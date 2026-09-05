package com.uade.tpo.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Double price;

    @Column
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Relación 1 a N con las imágenes
    @ToString.Exclude // Evita recursión infinita en el toString de Lombok
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    // Método helper para vincular ambos lados
    public void addImage(Image image) {
        images.add(image);
        image.setProduct(this);
    }
}