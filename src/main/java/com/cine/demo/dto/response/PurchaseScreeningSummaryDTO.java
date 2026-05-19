package com.cine.demo.dto.response;

import java.time.LocalDateTime;

public record PurchaseScreeningSummaryDTO(
        Long id,
        MovieSummary movie,
        TheaterSummary theater,
        LocalDateTime startTime
) {
    public record MovieSummary(String title) {}
    public record TheaterSummary(String name) {}
}
