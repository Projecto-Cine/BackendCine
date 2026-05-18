package com.cine.demo.config;

import com.cine.demo.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatReservationScheduler {

    private final ScreeningService screeningService;

    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredReservations() {
        screeningService.releaseExpiredReservations();
    }
}