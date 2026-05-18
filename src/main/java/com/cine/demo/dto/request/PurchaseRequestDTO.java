package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    private Long userId;

    @NotNull(message = "Screening is required")
    private Long screeningId;

    @NotEmpty(message = "Purchase must include at least one ticket")
    @Size(min = 1, message = "Purchase must include at least one ticket")
    @Valid
    private List<TicketRequestDTO> tickets;

    private PaymentMethod paymentMethod;

    @Email(message = "Guest email must be a valid email address")
    private String guestEmail;
}
