package com.cine.demo.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record TicketOfficeResponseDTO(
        Long saleId,
        List<String> qrCodes
) {}
