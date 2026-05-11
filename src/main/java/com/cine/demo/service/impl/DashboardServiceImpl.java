package com.cine.demo.service.impl;

import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.dto.response.YearlyDashboardResponseDTO;
import com.cine.demo.model.enums.BookingStatus;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.*;
import java.math.BigDecimal;
import com.cine.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final PurchaseRepository purchaseRepository;
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final MerchandiseSaleRepository merchandiseSaleRepository;

    @Override
    public DashboardResponseDTO getDashboardData() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        BigDecimal totalRevenue = purchaseRepository.sumTotalAmountByStatus(PurchaseStatus.PAID);
        BigDecimal weeklyRevenue = purchaseRepository.sumRevenueSince(PurchaseStatus.PAID, weekAgo);
        return DashboardResponseDTO.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .weeklyRevenue(weeklyRevenue != null ? weeklyRevenue : BigDecimal.ZERO)
                .totalPurchases(purchaseRepository.count())
                .paidPurchases(purchaseRepository.countByStatus(PurchaseStatus.PAID))
                .activeScreenings(screeningRepository.countByStartTimeAfter(LocalDateTime.now()))
                .confirmedRoomBookings(roomBookingRepository.countByStatus(BookingStatus.CONFIRMED))
                .totalUsers(userRepository.count())
                .activeMovies(movieRepository.countByActiveTrue())
                .unresolvedIncidents(incidentRepository.countByResolvedFalse())
                .build();
    }

    @Override
    public YearlyDashboardResponseDTO getYearlyData(int year) {
        BigDecimal ticketRevenue = purchaseRepository.sumRevenueByYear(PurchaseStatus.PAID, year);
        BigDecimal merchandiseRevenue = merchandiseSaleRepository.sumRevenueByYear(year);

        List<Object[]> topMovieRows = purchaseRepository.findTopMoviesByYear(PurchaseStatus.PAID, year);
        List<YearlyDashboardResponseDTO.TopMovieDTO> topMovies = new ArrayList<>();
        for (int i = 0; i < Math.min(3, topMovieRows.size()); i++) {
            Object[] row = topMovieRows.get(i);
            topMovies.add(YearlyDashboardResponseDTO.TopMovieDTO.builder()
                    .movieId((Long) row[0])
                    .movieTitle((String) row[1])
                    .revenue((BigDecimal) row[2])
                    .build());
        }

        List<Object[]> topProductRows = merchandiseSaleRepository.findTopMerchandiseByYear(year);
        List<YearlyDashboardResponseDTO.TopProductDTO> topProducts = new ArrayList<>();
        for (int i = 0; i < Math.min(3, topProductRows.size()); i++) {
            Object[] row = topProductRows.get(i);
            topProducts.add(YearlyDashboardResponseDTO.TopProductDTO.builder()
                    .productId((Long) row[0])
                    .productName((String) row[1])
                    .revenue((BigDecimal) row[2])
                    .build());
        }

        return YearlyDashboardResponseDTO.builder()
                .year(year)
                .moviesProjected(screeningRepository.countDistinctMoviesByYear(year))
                .sessionsProjected(screeningRepository.countByYear(year))
                .ticketRevenue(ticketRevenue != null ? ticketRevenue : BigDecimal.ZERO)
                .merchandiseRevenue(merchandiseRevenue != null ? merchandiseRevenue : BigDecimal.ZERO)
                .topMovies(topMovies)
                .topProducts(topProducts)
                .build();
    }
}
