package com.cine.demo.dashboard;

import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.dto.response.YearlyDashboardResponseDTO;
import com.cine.demo.model.enums.BookingStatus;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.*;
import com.cine.demo.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private PurchaseRepository purchaseRepository;
    @Mock private ScreeningRepository screeningRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private UserRepository userRepository;
    @Mock private IncidentRepository incidentRepository;
    @Mock private RoomBookingRepository roomBookingRepository;
    @Mock private MerchandiseSaleRepository merchandiseSaleRepository;

    @InjectMocks
    private DashboardServiceImpl service;

    private void stubDashboard(BigDecimal total, BigDecimal weekly) {
        when(purchaseRepository.sumTotalAmountByStatus(PurchaseStatus.PAID)).thenReturn(total);
        when(purchaseRepository.sumRevenueSince(eq(PurchaseStatus.PAID), any(LocalDateTime.class))).thenReturn(weekly);
        when(purchaseRepository.count()).thenReturn(50L);
        when(purchaseRepository.countByStatus(PurchaseStatus.PAID)).thenReturn(40L);
        when(screeningRepository.countByStartTimeAfter(any(LocalDateTime.class))).thenReturn(10L);
        when(roomBookingRepository.countByStatus(BookingStatus.CONFIRMED)).thenReturn(5L);
        when(userRepository.count()).thenReturn(100L);
        when(movieRepository.countByActiveTrue()).thenReturn(15L);
        when(incidentRepository.countByResolvedFalse()).thenReturn(3L);
    }

    @Test
    void getDashboardData_returnsKPIs() {
        stubDashboard(BigDecimal.valueOf(1000), BigDecimal.valueOf(200));

        DashboardResponseDTO result = service.getDashboardData();

        assertThat(result.totalRevenue()).isEqualByComparingTo("1000");
        assertThat(result.totalPurchases()).isEqualTo(50);
        assertThat(result.activeMovies()).isEqualTo(15);
    }

    @Test
    void getDashboardData_returnsAllKPIFields() {
        stubDashboard(BigDecimal.valueOf(5000), BigDecimal.valueOf(300));

        DashboardResponseDTO result = service.getDashboardData();

        assertThat(result.weeklyRevenue()).isEqualByComparingTo("300");
        assertThat(result.paidPurchases()).isEqualTo(40);
        assertThat(result.activeScreenings()).isEqualTo(10);
        assertThat(result.confirmedRoomBookings()).isEqualTo(5);
        assertThat(result.totalUsers()).isEqualTo(100);
        assertThat(result.unresolvedIncidents()).isEqualTo(3);
    }

    @Test
    void getDashboardData_usesZeroWhenTotalRevenueIsNull() {
        stubDashboard(null, BigDecimal.valueOf(200));

        DashboardResponseDTO result = service.getDashboardData();

        assertThat(result.totalRevenue()).isEqualByComparingTo("0");
    }

    @Test
    void getDashboardData_usesZeroWhenWeeklyRevenueIsNull() {
        stubDashboard(BigDecimal.valueOf(1000), null);

        DashboardResponseDTO result = service.getDashboardData();

        assertThat(result.weeklyRevenue()).isEqualByComparingTo("0");
    }

    @Test
    void getYearlyData_returnsYearlyStats() {
        when(purchaseRepository.sumRevenueByYear(PurchaseStatus.PAID, 2025))
                .thenReturn(BigDecimal.valueOf(12000));
        when(merchandiseSaleRepository.sumRevenueByYear(2025))
                .thenReturn(BigDecimal.valueOf(3000));
        List<Object[]> topMovies = List.<Object[]>of(
                new Object[]{1L, "Inception", BigDecimal.valueOf(5000)},
                new Object[]{2L, "Dune", BigDecimal.valueOf(4000)});
        List<Object[]> topProducts = List.<Object[]>of(
                new Object[]{10L, "Popcorn", BigDecimal.valueOf(2000)});
        when(purchaseRepository.findTopMoviesByYear(PurchaseStatus.PAID, 2025)).thenReturn(topMovies);
        when(merchandiseSaleRepository.findTopMerchandiseByYear(2025)).thenReturn(topProducts);
        when(screeningRepository.countDistinctMoviesByYear(2025)).thenReturn(20L);
        when(screeningRepository.countByYear(2025)).thenReturn(150L);

        YearlyDashboardResponseDTO result = service.getYearlyData(2025);

        assertThat(result.year()).isEqualTo(2025);
        assertThat(result.ticketRevenue()).isEqualByComparingTo("12000");
        assertThat(result.merchandiseRevenue()).isEqualByComparingTo("3000");
        assertThat(result.moviesProjected()).isEqualTo(20);
        assertThat(result.sessionsProjected()).isEqualTo(150);
        assertThat(result.topMovies()).hasSize(2);
        assertThat(result.topMovies().get(0).movieTitle()).isEqualTo("Inception");
        assertThat(result.topProducts()).hasSize(1);
        assertThat(result.topProducts().get(0).productName()).isEqualTo("Popcorn");
    }

    @Test
    void getYearlyData_usesZeroWhenRevenueIsNull() {
        when(purchaseRepository.sumRevenueByYear(PurchaseStatus.PAID, 2024)).thenReturn(null);
        when(merchandiseSaleRepository.sumRevenueByYear(2024)).thenReturn(null);
        when(purchaseRepository.findTopMoviesByYear(PurchaseStatus.PAID, 2024)).thenReturn(List.of());
        when(merchandiseSaleRepository.findTopMerchandiseByYear(2024)).thenReturn(List.of());
        when(screeningRepository.countDistinctMoviesByYear(2024)).thenReturn(0L);
        when(screeningRepository.countByYear(2024)).thenReturn(0L);

        YearlyDashboardResponseDTO result = service.getYearlyData(2024);

        assertThat(result.ticketRevenue()).isEqualByComparingTo("0");
        assertThat(result.merchandiseRevenue()).isEqualByComparingTo("0");
    }

    @Test
    void getYearlyData_limitsTopMoviesTo3() {
        List<Object[]> fourMovies = List.<Object[]>of(
                new Object[]{1L, "Movie A", BigDecimal.valueOf(5000)},
                new Object[]{2L, "Movie B", BigDecimal.valueOf(4000)},
                new Object[]{3L, "Movie C", BigDecimal.valueOf(3000)},
                new Object[]{4L, "Movie D", BigDecimal.valueOf(2000)});
        when(purchaseRepository.sumRevenueByYear(PurchaseStatus.PAID, 2025)).thenReturn(BigDecimal.ZERO);
        when(merchandiseSaleRepository.sumRevenueByYear(2025)).thenReturn(BigDecimal.ZERO);
        when(purchaseRepository.findTopMoviesByYear(PurchaseStatus.PAID, 2025)).thenReturn(fourMovies);
        when(merchandiseSaleRepository.findTopMerchandiseByYear(2025)).thenReturn(List.of());
        when(screeningRepository.countDistinctMoviesByYear(2025)).thenReturn(0L);
        when(screeningRepository.countByYear(2025)).thenReturn(0L);

        YearlyDashboardResponseDTO result = service.getYearlyData(2025);

        assertThat(result.topMovies()).hasSize(3);
    }
}
