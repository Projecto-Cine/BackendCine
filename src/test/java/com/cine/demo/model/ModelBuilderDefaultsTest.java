package com.cine.demo.model;

import com.cine.demo.model.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ModelBuilderDefaultsTest {

    @Test
    void purchase_builderDefaults() {
        Purchase p = Purchase.builder().totalAmount(BigDecimal.TEN).build();
        assertThat(p.getStatus()).isEqualTo(PurchaseStatus.PENDING);
        assertThat(p.getTickets()).isEmpty();
        assertThat(p.isDiscountApplied()).isFalse();
        assertThat(p.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(p.isEmailSent()).isFalse();
    }

    @Test
    void movie_builderDefaults() {
        Movie m = Movie.builder().title("Test").genre("Drama").durationMin(90).build();
        assertThat(m.isActive()).isTrue();
        assertThat(m.getFormat()).isEqualTo("2D");
    }

    @Test
    void user_builderDefaults() {
        User u = User.builder().name("Alice").email("alice@test.com").build();
        assertThat(u.getAnnualVisits()).isZero();
        assertThat(u.isDiscountActive()).isFalse();
        assertThat(u.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void screeningSeat_builderDefault_occupiedIsFalse() {
        ScreeningSeat ss = ScreeningSeat.builder().build();
        assertThat(ss.isOccupied()).isFalse();
    }

    @Test
    void screening_builderDefaults() {
        Screening s = Screening.builder()
                .basePrice(BigDecimal.TEN)
                .startTime(LocalDateTime.now())
                .build();
        assertThat(s.getOccupiedSeats()).isZero();
        assertThat(s.isFull()).isFalse();
        assertThat(s.getScreeningSeats()).isEmpty();
    }

    @Test
    void employee_builderDefault_activeIsTrue() {
        Employee e = Employee.builder()
                .name("Bob").email("bob@test.com").password("pwd").role(EmployeeRole.MANAGEMENT)
                .build();
        assertThat(e.isActive()).isTrue();
    }

    @Test
    void merchandise_builderDefaults() {
        Merchandise m = Merchandise.builder().build();
        assertThat(m.getStock()).isZero();
        assertThat(m.getMinStock()).isZero();
        assertThat(m.isActive()).isTrue();
    }

    @Test
    void room_builderDefaults() {
        Room r = Room.builder().name("Hall A").capacity(50).build();
        assertThat(r.getRoomType()).isEqualTo(RoomType.STANDARD);
        assertThat(r.isActive()).isTrue();
    }

    @Test
    void roomBooking_builderDefault_statusIsPending() {
        RoomBooking rb = RoomBooking.builder()
                .bookingDate(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        assertThat(rb.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void theater_builderDefault_seatsIsEmpty() {
        Theater t = Theater.builder().name("Cinema 1").capacity(200).build();
        assertThat(t.getSeats()).isEmpty();
    }

    @Test
    void shift_builderDefault_statusIsScheduled() {
        Shift s = Shift.builder()
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        assertThat(s.getStatus()).isEqualTo(ShiftStatus.SCHEDULED);
    }

    @Test
    void incident_builderDefault_statusIsOpen() {
        Incident i = Incident.builder().title("Broken seat").severity("LOW").build();
        assertThat(i.getStatus()).isEqualTo(IncidentStatus.OPEN);
    }
}