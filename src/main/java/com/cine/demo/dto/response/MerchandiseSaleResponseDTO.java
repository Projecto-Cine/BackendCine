package com.cine.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MerchandiseSaleResponseDTO {

    @JsonProperty("sale_id")
    private Long saleId;

    private Double total;
    private String paymentMethod;
    private Double cashGiven;
    private Double change;
    private LocalDateTime createdAt;
}