package com.cine.demo.dto.request;

import com.cine.demo.model.enums.MerchandiseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseRequestDTO {
    private String name;
    private String description;
    private MerchandiseCategory category;
    private Double price;
    private Integer stock;
    private String imageUrl;
}
