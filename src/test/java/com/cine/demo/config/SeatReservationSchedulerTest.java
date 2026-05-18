package com.cine.demo.config;

import com.cine.demo.service.ScreeningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeatReservationSchedulerTest {

    @Mock private ScreeningService screeningService;

    @InjectMocks
    private SeatReservationScheduler scheduler;

    @Test
    void releaseExpiredReservations_delegatesToScreeningService() {
        scheduler.releaseExpiredReservations();

        verify(screeningService).releaseExpiredReservations();
    }
}
