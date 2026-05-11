package com.cine.demo.service.impl;

import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.model.enums.BookingStatus;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.*;
import java.math.BigDecimal;
import com.cine.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

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
}
