package com.cine.demo.service.impl;

import com.cine.demo.dto.response.*;
import com.cine.demo.model.Screening;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.*;
import com.cine.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final PurchaseRepository purchaseRepository;
    private final ScreeningRepository screeningRepository;
    private final TheaterRepository theaterRepository;
    private final IncidentRepository incidentRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final UserRepository userRepository;

    @Override
    public KpiResponseDTO getKpis() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long ticketsToday = purchaseRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null &&
                             p.getCreatedAt().isAfter(startOfDay) &&
                             p.getCreatedAt().isBefore(endOfDay))
                .mapToLong(p -> p.getTickets().size())
                .sum();

        double revenueToday = purchaseRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null &&
                             p.getCreatedAt().isAfter(startOfDay) &&
                             p.getCreatedAt().isBefore(endOfDay) &&
                             p.getTotalAmount() != null)
                .mapToDouble(p -> p.getTotalAmount().doubleValue())
                .sum();

        long incidentsOpen = incidentRepository.findByStatus("open").size();

        List<Screening> activeNow = screeningRepository.findAll().stream()
                .filter(s -> s.getDateTime() != null &&
                             s.getDateTime().isBefore(LocalDateTime.now()) &&
                             s.getDateTime().plusHours(3).isAfter(LocalDateTime.now()))
                .toList();

        long reservationsToday = purchaseRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null &&
                             p.getCreatedAt().isAfter(startOfDay) &&
                             p.getCreatedAt().isBefore(endOfDay))
                .count();

        long operationalRooms = theaterRepository.findAll().stream()
                .filter(t -> "active".equals(t.getStatus()))
                .count();

        List<Theater> theaters = theaterRepository.findAll();
        double avgOccupancy = theaters.isEmpty() ? 0 : theaters.stream()
                .mapToDouble(t -> {
                    if (t.getCapacity() == 0) return 0;
                    List<Screening> theaterScreenings = screeningRepository.findByTheaterId(t.getId());
                    if (theaterScreenings.isEmpty()) return 0;
                    double avg = theaterScreenings.stream()
                            .mapToInt(s -> screeningSeatRepository.countByScreeningIdAndOccupiedTrue(s.getId()))
                            .average().orElse(0);
                    return avg / t.getCapacity() * 100;
                })
                .average()
                .orElse(0);

        long totalClients = userRepository.findByRole(Role.CLIENT).size();

        return KpiResponseDTO.builder()
                .revenueToday(revenueToday)
                .ticketsToday((int) ticketsToday)
                .occupancyAvg(Math.round(avgOccupancy * 10.0) / 10.0)
                .incidentsOpen((int) incidentsOpen)
                .activeSessions(activeNow.size())
                .reservationsToday((int) reservationsToday)
                .operationalRooms((int) operationalRooms)
                .totalClients((int) totalClients)
                .build();
    }

    @Override
    public List<SalesWeekItemDTO> getSalesWeek() {
        LocalDateTime now = LocalDateTime.now();
        List<SalesWeekItemDTO> result = new ArrayList<>();

        String[] days = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            int dayOfWeek = dayStart.getDayOfWeek().getValue() - 1;

            double ventas = purchaseRepository.findAll().stream()
                    .filter(p -> p.getCreatedAt() != null &&
                                 p.getCreatedAt().isAfter(dayStart) &&
                                 p.getCreatedAt().isBefore(dayEnd) &&
                                 p.getTotalAmount() != null)
                    .mapToDouble(p -> p.getTotalAmount().doubleValue())
                    .sum();

            long entradas = purchaseRepository.findAll().stream()
                    .filter(p -> p.getCreatedAt() != null &&
                                 p.getCreatedAt().isAfter(dayStart) &&
                                 p.getCreatedAt().isBefore(dayEnd))
                    .mapToLong(p -> p.getTickets().size())
                    .sum();

            result.add(SalesWeekItemDTO.builder()
                    .day(days[dayOfWeek])
                    .ventas(ventas)
                    .entradas((int) entradas)
                    .build());
        }
        return result;
    }

    @Override
    public List<OccupancyItemDTO> getOccupancy() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        return theaterRepository.findAll().stream()
                .map(theater -> {
                    List<Screening> todayScreenings = screeningRepository
                            .findByDateTimeBetween(todayStart, todayEnd).stream()
                            .filter(s -> s.getTheater() != null && s.getTheater().getId().equals(theater.getId()))
                            .toList();

                    int capacity = theater.getCapacity();
                    int sold = todayScreenings.stream()
                            .mapToInt(s -> screeningSeatRepository.countByScreeningIdAndOccupiedTrue(s.getId()))
                            .sum();
                    int totalCapacity = capacity * Math.max(todayScreenings.size(), 1);
                    int pct = totalCapacity > 0 ? (int) Math.round((double) sold / totalCapacity * 100) : 0;

                    return OccupancyItemDTO.builder()
                            .sala(theater.getName())
                            .pct(pct)
                            .sold(sold)
                            .capacity(capacity * Math.max(todayScreenings.size(), 1))
                            .build();
                })
                .toList();
    }

    @Override
    public List<CategoryReportDTO> getIncidentsByCategory() {
        Map<String, Long> byCategory = incidentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCategory() != null ? i.getCategory() : "Sin categoría",
                        Collectors.counting()
                ));
        return byCategory.entrySet().stream()
                .map(e -> CategoryReportDTO.builder()
                        .category(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();
    }

    @Override
    public List<FormatPerformanceDTO> getFormatPerformance() {
        List<Screening> all = screeningRepository.findAll();
        Map<String, List<Screening>> byFormat = all.stream()
                .filter(s -> s.getMovie() != null && s.getMovie().getFormat() != null)
                .collect(Collectors.groupingBy(s -> s.getMovie().getFormat()));

        return byFormat.entrySet().stream()
                .map(e -> {
                    String format = e.getKey();
                    List<Screening> screenings = e.getValue();
                    long totalTickets = screenings.stream()
                            .mapToInt(s -> screeningSeatRepository.countByScreeningIdAndOccupiedTrue(s.getId()))
                            .asLongStream()
                            .sum();
                    double totalRevenue = screenings.stream()
                            .mapToDouble(s -> {
                                int occ = screeningSeatRepository.countByScreeningIdAndOccupiedTrue(s.getId());
                                return occ * (s.getBasePrice() != null ? s.getBasePrice().doubleValue() : 0);
                            })
                            .sum();
                    int avgOccupancy = screenings.isEmpty() ? 0 : (int) screenings.stream()
                            .mapToDouble(s -> {
                                if (s.getTheater() == null || s.getTheater().getCapacity() == 0) return 0;
                                return (double) screeningSeatRepository.countByScreeningIdAndOccupiedTrue(s.getId())
                                        / s.getTheater().getCapacity() * 100;
                            })
                            .average()
                            .orElse(0);

                    return FormatPerformanceDTO.builder()
                            .format(format)
                            .sessions(screenings.size())
                            .tickets(totalTickets)
                            .revenue(totalRevenue)
                            .occupancy(avgOccupancy)
                            .build();
                })
                .toList();
    }
}