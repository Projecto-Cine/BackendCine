package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketOfficeRequestDTO {

    @NotNull(message = "Screening is required")
    private Long screeningId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seats;

    @NotNull(message = "Ticket type is required")
    private String ticketType;

    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    private BigDecimal surcharge;

    @NotNull(message = "Total is required")
    private BigDecimal total;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Cashier is required")
    private Long cashierId;

    private Long userId;
}
