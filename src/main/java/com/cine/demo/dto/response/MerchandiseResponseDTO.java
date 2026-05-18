package com.cine.demo.dto.response;

import com.cine.demo.model.enums.MerchandiseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private MerchandiseCategory category;
    private Double price;
    private Integer stock;
    private Integer minStock;
    private String supplier;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}
