package com.uade.tpo.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class Order {

    @Builder
    public Order(Double total, Long user_id) {
        this.total = total;
        this.user_id = user_id;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String status;

    @Column
    private Double total;

    @Column
    private LocalDateTime createdAt;

    @JoinColumn(name = "user_id", nullable = false)
    private Long user_id;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
}