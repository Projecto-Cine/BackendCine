package com.cine.demo.service.impl;

import com.cine.demo.dto.response.OccupancyResponseDTO;
import com.cine.demo.dto.response.SalesWeekResponseDTO;
import com.cine.demo.model.Purchase;
import com.cine.demo.model.Screening;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.ScreeningRepository;
import com.cine.demo.repository.ScreeningSeatRepository;
import com.cine.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final PurchaseRepository purchaseRepository;
    private final ScreeningRepository screeningRepository;
    private final ScreeningSeatRepository screeningSeatRepository;

    @Override
    public List<SalesWeekResponseDTO> getSalesWeek() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(6).toLocalDate().atStartOfDay();
        LocalDateTime weekEnd = LocalDateTime.now();

        List<Purchase> purchases = purchaseRepository
                .findByStatusAndCreatedAtBetween(PurchaseStatus.PAID, weekStart, weekEnd);

        Map<LocalDate, List<Purchase>> byDay = purchases.stream()
                .collect(Collectors.groupingBy(p -> p.getCreatedAt().toLocalDate()));

        List<SalesWeekResponseDTO> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            List<Purchase> dayPurchases = byDay.getOrDefault(day, List.of());
            BigDecimal revenue = dayPurchases.stream()
                    .map(Purchase::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(SalesWeekResponseDTO.builder()
                    .date(day)
                    .totalPurchases(dayPurchases.size())
                    .revenue(revenue)
                    .build());
        }
        return result;
    }

    @Override
    public List<OccupancyResponseDTO> getOccupancy() {
        return screeningRepository.findAllWithMovieAndTheater().stream()
                .map(this::toOccupancyDto)
                .toList();
    }

    private OccupancyResponseDTO toOccupancyDto(Screening screening) {
        int totalSeats = screening.getTheater().getCapacity();
        int occupiedSeats = screeningSeatRepository.countByScreeningIdAndOccupiedTrue(screening.getId());
        double percentage = totalSeats > 0 ? (occupiedSeats * 100.0 / totalSeats) : 0.0;
        return OccupancyResponseDTO.builder()
                .screeningId(screening.getId())
                .movieTitle(screening.getMovie().getTitle())
                .theaterName(screening.getTheater().getName())
                .startTime(screening.getStartTime())
                .totalSeats(totalSeats)
                .occupiedSeats(occupiedSeats)
                .occupancyPercentage(Math.round(percentage * 10.0) / 10.0)
                .build();
    }
}
