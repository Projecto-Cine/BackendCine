package com.cine.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchandise_sale_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseSaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MerchandiseSale sale;

    private Long productId;

    private String name;

    private Integer qty;

    private Double unitPrice;
}