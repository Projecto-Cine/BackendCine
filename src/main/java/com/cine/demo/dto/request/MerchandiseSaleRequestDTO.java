package com.cine.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class MerchandiseSaleRequestDTO {

    private List<SaleItemDTO> items;

    private Double total;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cash_given")
    private Double cashGiven;

    private Double change;

    @JsonProperty("cashier_id")
    private Long cashierId;

    @Data
    public static class SaleItemDTO {
        @JsonProperty("product_id")
        private Long productId;
        private String name;
        private Integer qty;
        @JsonProperty("unit_price")
        private Double unitPrice;
    }
}