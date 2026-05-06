package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TicketOfficeRequestDTO {

    @NotNull(message = "Screening is required")
    private Long screeningId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seats;

    private String ticketType;
    private String formatExtra;
    private Double unitPrice;
    private Double surcharge;
    private Double total;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private Long cashierId;
}