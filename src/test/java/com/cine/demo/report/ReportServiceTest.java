package com.cine.demo.report;

import com.cine.demo.dto.response.OccupancyResponseDTO;
import com.cine.demo.dto.response.SalesWeekResponseDTO;
import com.cine.demo.model.Movie;
import com.cine.demo.model.Purchase;
import com.cine.demo.model.Screening;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.ScreeningRepository;
import com.cine.demo.repository.ScreeningSeatRepository;
import com.cine.demo.service.impl.ReportServiceImpl;
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
class ReportServiceTest {

    @Mock private PurchaseRepository purchaseRepository;
    @Mock private ScreeningRepository screeningRepository;
    @Mock private ScreeningSeatRepository screeningSeatRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void getSalesWeek_returnsSevenDays() {
        when(purchaseRepository.findByStatusAndCreatedAtBetween(eq(PurchaseStatus.PAID), any(), any()))
                .thenReturn(List.of());

        List<SalesWeekResponseDTO> result = reportService.getSalesWeek();

        assertThat(result).hasSize(7);
    }

    @Test
    void getSalesWeek_aggregatesRevenueByDay() {
        Purchase p1 = Purchase.builder()
                .totalAmount(new BigDecimal("50.00"))
                .status(PurchaseStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();
        Purchase p2 = Purchase.builder()
                .totalAmount(new BigDecimal("30.00"))
                .status(PurchaseStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();
        when(purchaseRepository.findByStatusAndCreatedAtBetween(eq(PurchaseStatus.PAID), any(), any()))
                .thenReturn(List.of(p1, p2));

        List<SalesWeekResponseDTO> result = reportService.getSalesWeek();

        SalesWeekResponseDTO today = result.get(result.size() - 1);
        assertThat(today.getTotalPurchases()).isEqualTo(2);
        assertThat(today.getRevenue()).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void getSalesWeek_returnsZeroRevenue_forDaysWithNoPurchases() {
        when(purchaseRepository.findByStatusAndCreatedAtBetween(eq(PurchaseStatus.PAID), any(), any()))
                .thenReturn(List.of());

        List<SalesWeekResponseDTO> result = reportService.getSalesWeek();

        assertThat(result).allMatch(d -> d.getRevenue().compareTo(BigDecimal.ZERO) == 0);
        assertThat(result).allMatch(d -> d.getTotalPurchases() == 0);
    }

    @Test
    void getOccupancy_returnsOccupancyPerScreening() {
        Theater theater = Theater.builder().id(1L).name("Hall A").capacity(100).build();
        Movie movie = Movie.builder().id(1L).title("Inception").build();
        Screening screening = Screening.builder().id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.of(2026, 5, 13, 20, 0)).build();
        when(screeningRepository.findAllWithMovieAndTheater()).thenReturn(List.of(screening));
        when(screeningSeatRepository.countByScreeningIdAndOccupiedTrue(1L)).thenReturn(75);

        List<OccupancyResponseDTO> result = reportService.getOccupancy();

        assertThat(result).hasSize(1);
        OccupancyResponseDTO dto = result.get(0);
        assertThat(dto.getMovieTitle()).isEqualTo("Inception");
        assertThat(dto.getTheaterName()).isEqualTo("Hall A");
        assertThat(dto.getTotalSeats()).isEqualTo(100);
        assertThat(dto.getOccupiedSeats()).isEqualTo(75);
        assertThat(dto.getOccupancyPercentage()).isEqualTo(75.0);
    }

    @Test
    void getOccupancy_returnsZeroPercentage_whenTheaterHasNoCapacity() {
        Theater theater = Theater.builder().id(1L).name("Hall B").capacity(0).build();
        Movie movie = Movie.builder().id(1L).title("Dune").build();
        Screening screening = Screening.builder().id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.now()).build();
        when(screeningRepository.findAllWithMovieAndTheater()).thenReturn(List.of(screening));
        when(screeningSeatRepository.countByScreeningIdAndOccupiedTrue(1L)).thenReturn(0);

        List<OccupancyResponseDTO> result = reportService.getOccupancy();

        assertThat(result.get(0).getOccupancyPercentage()).isEqualTo(0.0);
    }

    @Test
    void getOccupancy_returnsEmpty_whenNoScreenings() {
        when(screeningRepository.findAllWithMovieAndTheater()).thenReturn(List.of());

        assertThat(reportService.getOccupancy()).isEmpty();
    }
}
