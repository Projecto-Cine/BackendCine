package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record YearlyDashboardResponseDTO(
        int year,
        long moviesProjected,
        long sessionsProjected,
        BigDecimal ticketRevenue,
        BigDecimal merchandiseRevenue,
        List<TopMovieDTO> topMovies,
        List<TopProductDTO> topProducts
) {
    @Builder
    public record TopMovieDTO(
            Long movieId,
            String movieTitle,
            BigDecimal revenue
    ) {}

    @Builder
    public record TopProductDTO(
            Long productId,
            String productName,
            BigDecimal revenue
    ) {}
}
