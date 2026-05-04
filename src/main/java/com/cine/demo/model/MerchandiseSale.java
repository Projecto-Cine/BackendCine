package com.cine.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "merchandise_sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchandiseSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MerchandiseSaleItem> items = new ArrayList<>();

    private Double total;

    private String paymentMethod;

    private Double cashGiven;

    private Double change;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User cashier;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}