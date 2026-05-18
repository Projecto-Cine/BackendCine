package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MerchandiseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private int stock;
    private int minStock;
    private String supplier;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
}
