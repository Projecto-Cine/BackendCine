package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class YearlyDashboardResponseDTO {
    private int year;
    private long moviesProjected;
    private long sessionsProjected;
    private BigDecimal ticketRevenue;
    private BigDecimal merchandiseRevenue;
    private List<TopMovieDTO> topMovies;
    private List<TopProductDTO> topProducts;

    @Data
    @Builder
    public static class TopMovieDTO {
        private Long movieId;
        private String movieTitle;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class TopProductDTO {
        private Long productId;
        private String productName;
        private BigDecimal revenue;
    }
}
