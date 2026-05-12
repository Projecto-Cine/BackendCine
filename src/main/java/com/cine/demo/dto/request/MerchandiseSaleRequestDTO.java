package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MerchandiseSaleRequestDTO {

    private Long userId;

    private Long merchandiseId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int quantity;

    private List<Item> items;

    private BigDecimal total;

    private String paymentMethod;

    private String payment_method;

    @Data
    public static class Item {
        private Long merchandiseId;
        private Long productId;
        private Long product_id;

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private int quantity;

        private int qty;
    }
}
