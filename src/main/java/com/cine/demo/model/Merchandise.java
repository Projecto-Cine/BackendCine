package com.cine.demo.model;

import com.cine.demo.model.enums.MerchandiseCategory;
import com.cine.demo.model.enums.MerchandiseCategoryConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchandise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Convert(converter = MerchandiseCategoryConverter.class)
    @Column(nullable = false)
    private MerchandiseCategory category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}
