package com.cine.demo.dashboard;

import com.cine.demo.dto.response.DashboardResponseDTO;
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

    @InjectMocks
    private DashboardServiceImpl service;

    @Test
    void getDashboardData_returnsKPIs() {
        when(purchaseRepository.sumTotalAmountByStatus(PurchaseStatus.PAID)).thenReturn(BigDecimal.valueOf(1000));
        when(purchaseRepository.sumRevenueSince(eq(PurchaseStatus.PAID), any(LocalDateTime.class))).thenReturn(BigDecimal.valueOf(200));
        when(purchaseRepository.count()).thenReturn(50L);
        when(purchaseRepository.countByStatus(PurchaseStatus.PAID)).thenReturn(40L);
        when(screeningRepository.countByFechaHoraAfter(any(LocalDateTime.class))).thenReturn(10L);
        when(roomBookingRepository.countByStatus(BookingStatus.CONFIRMED)).thenReturn(5L);
        when(userRepository.count()).thenReturn(100L);
        when(movieRepository.countByActiveTrue()).thenReturn(15L);
        when(incidentRepository.countByResolvedFalse()).thenReturn(3L);

        DashboardResponseDTO result = service.getDashboardData();

        assertThat(result.getTotalRevenue()).isEqualByComparingTo("1000");
        assertThat(result.getTotalPurchases()).isEqualTo(50);
        assertThat(result.getActiveMovies()).isEqualTo(15);
    }
}
