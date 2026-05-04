package com.cine.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class TaquillaRequestDTO {

    @JsonProperty("session_id")
    @NotNull(message = "La proyección es obligatoria")
    private Long sessionId;

    @NotEmpty(message = "Debe seleccionar al menos un asiento")
    private List<String> seats;

    @JsonProperty("ticket_type")
    private String ticketType;

    @JsonProperty("format_extra")
    private String formatExtra;

    @JsonProperty("unit_price")
    private Double unitPrice;

    private Double surcharge;

    private Double total;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cashier_id")
    private Long cashierId;
}