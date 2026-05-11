package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TicketOfficeResponseDTO {
    private Long saleId;
    private List<String> qrCodes;
}
