package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record TicketQrDTO(
        Long ticketId,
        String qrCode
) {}
