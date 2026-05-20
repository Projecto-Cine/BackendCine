package com.cine.demo.dto;

import com.cine.demo.dto.response.*;
import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.model.enums.TicketType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseDtoTest {

    // ── ApiResponse ───────────────────────────────────────────────────────────

    @Test
    void apiResponse_ok_withData_setsSuccessTrueAndData() {
        ApiResponse<String> response = ApiResponse.ok("Retrieved", "payload");
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Retrieved");
        assertThat(response.data()).isEqualTo("payload");
    }

    @Test
    void apiResponse_ok_withoutData_setsNullData() {
        ApiResponse<Void> response = ApiResponse.ok("Done");
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Done");
        assertThat(response.data()).isNull();
    }

    @Test
    void apiResponse_directConstruction_setsAllFields() {
        ApiResponse<Integer> response = new ApiResponse<>(false, "Error", null);
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Error");
        assertThat(response.data()).isNull();
    }

    // ── ApiError ──────────────────────────────────────────────────────────────

    @Test
    void apiError_withMessage_setsMessageAndAutoTimestamp() {
        ApiError error = new ApiError("Something went wrong");
        assertThat(error.message()).isEqualTo("Something went wrong");
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void apiError_withMessageAndTimestamp_setsExactTimestamp() {
        LocalDateTime ts = LocalDateTime.of(2025, 6, 15, 12, 0, 0);
        ApiError error = new ApiError("Conflict", ts);
        assertThat(error.message()).isEqualTo("Conflict");
        assertThat(error.timestamp()).isEqualTo(ts);
    }

    // ── AuthResponseDTO ───────────────────────────────────────────────────────

    @Test
    void authResponseDTO_builderSetsAllFields() {
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .token("jwt.token.here").tokenType("Bearer")
                .expiresInSeconds(3600L).userId(1L)
                .email("ana@test.com").role("CLIENT").build();
        assertThat(dto.token()).isEqualTo("jwt.token.here");
        assertThat(dto.tokenType()).isEqualTo("Bearer");
        assertThat(dto.expiresInSeconds()).isEqualTo(3600L);
        assertThat(dto.userId()).isEqualTo(1L);
        assertThat(dto.email()).isEqualTo("ana@test.com");
        assertThat(dto.role()).isEqualTo("CLIENT");
    }

    // ── LoginResponseDTO ──────────────────────────────────────────────────────

    @Test
    void loginResponseDTO_builderSetsTokenAndUser() {
        LoginResponseDTO.UserInfo userInfo = LoginResponseDTO.UserInfo.builder()
                .id(1L).name("Ana").email("ana@test.com").role("CLIENT")
                .imageUrl("https://img.png").status("active").build();
        LoginResponseDTO dto = LoginResponseDTO.builder()
                .token("jwt.abc").user(userInfo).build();
        assertThat(dto.token()).isEqualTo("jwt.abc");
        assertThat(dto.user().name()).isEqualTo("Ana");
        assertThat(dto.user().role()).isEqualTo("CLIENT");
    }

    // ── UserResponseDTO ───────────────────────────────────────────────────────

    @Test
    void userResponseDTO_builderSetsKeyFields() {
        UserResponseDTO dto = UserResponseDTO.builder()
                .id(1L).name("Ana").email("ana@test.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .annualVisits(5).discountActive(false).role("CLIENT").build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@test.com");
        assertThat(dto.annualVisits()).isEqualTo(5);
        assertThat(dto.discountActive()).isFalse();
    }

    // ── EmployeeResponseDTO ───────────────────────────────────────────────────

    @Test
    void employeeResponseDTO_builderSetsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        EmployeeResponseDTO dto = EmployeeResponseDTO.builder()
                .id(1L).name("Carlos").email("carlos@cine.com")
                .role("CAJERO").phoneNumber("123456789").createdAt(now).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Carlos");
        assertThat(dto.email()).isEqualTo("carlos@cine.com");
        assertThat(dto.role()).isEqualTo("CAJERO");
        assertThat(dto.createdAt()).isEqualTo(now);
    }

    // ── MovieResponseDTO ──────────────────────────────────────────────────────

    @Test
    void movieResponseDTO_builderSetsKeyFields() {
        MovieResponseDTO dto = MovieResponseDTO.builder()
                .id(1L).title("Inception").genre("Sci-Fi")
                .durationMin(148).ageRating("12").active(true).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Inception");
        assertThat(dto.durationMin()).isEqualTo(148);
        assertThat(dto.active()).isTrue();
    }

    // ── TheaterResponseDTO ────────────────────────────────────────────────────

    @Test
    void theaterResponseDTO_builderSetsAllFields() {
        TheaterResponseDTO dto = TheaterResponseDTO.builder()
                .id(1L).name("Sala 1").capacity(100)
                .numRows(10).numColumns(10).totalSeats(100).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Sala 1");
        assertThat(dto.capacity()).isEqualTo(100);
        assertThat(dto.totalSeats()).isEqualTo(100);
    }

    // ── SeatResponseDTO ───────────────────────────────────────────────────────

    @Test
    void seatResponseDTO_builderSetsAllFields() {
        SeatResponseDTO dto = SeatResponseDTO.builder()
                .id(1L).theaterId(1L).row("A").number(5).type("STANDARD").build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.row()).isEqualTo("A");
        assertThat(dto.number()).isEqualTo(5);
        assertThat(dto.type()).isEqualTo("STANDARD");
    }

    // ── ScreeningResponseDTO ──────────────────────────────────────────────────

    @Test
    void screeningResponseDTO_builderSetsKeyFields() {
        MovieResponseDTO movie = MovieResponseDTO.builder().id(1L).title("Matrix").build();
        TheaterResponseDTO theater = TheaterResponseDTO.builder().id(1L).name("Sala 2").build();
        LocalDateTime start = LocalDateTime.of(2025, 8, 15, 20, 0);
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder()
                .id(1L).movie(movie).theater(theater)
                .startTime(start).basePrice(BigDecimal.TEN)
                .availableSeats(80).full(false).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.movie().title()).isEqualTo("Matrix");
        assertThat(dto.theater().name()).isEqualTo("Sala 2");
        assertThat(dto.full()).isFalse();
    }

    // ── ScreeningSeatResponseDTO ──────────────────────────────────────────────

    @Test
    void screeningSeatResponseDTO_builderSetsAllFields() {
        SeatResponseDTO seat = SeatResponseDTO.builder()
                .id(1L).row("B").number(3).type("VIP").build();
        ScreeningSeatResponseDTO dto = ScreeningSeatResponseDTO.builder()
                .id(1L).screeningId(2L).seat(seat).occupied(false).status("available").build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.screeningId()).isEqualTo(2L);
        assertThat(dto.seat().row()).isEqualTo("B");
        assertThat(dto.occupied()).isFalse();
        assertThat(dto.status()).isEqualTo("available");
    }

    // ── TicketResponseDTO ─────────────────────────────────────────────────────

    @Test
    void ticketResponseDTO_builderSetsAllFields() {
        TicketResponseDTO dto = TicketResponseDTO.builder()
                .id(1L).purchaseId(10L).seatId(5L)
                .row("A").number(3).seatType("STANDARD")
                .ticketType(TicketType.ADULT).unitPrice(new BigDecimal("10.00")).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.ticketType()).isEqualTo(TicketType.ADULT);
        assertThat(dto.unitPrice()).isEqualByComparingTo("10.00");
        assertThat(dto.row()).isEqualTo("A");
    }

    // ── PurchaseResponseDTO ───────────────────────────────────────────────────

    @Test
    void purchaseResponseDTO_builderSetsKeyFields() {
        PurchaseResponseDTO dto = PurchaseResponseDTO.builder()
                .id(1L).userId(2L).userName("Ana").screeningId(3L)
                .movieTitle("Inception").theaterName("Sala 1")
                .startTime(LocalDateTime.now().plusDays(1))
                .tickets(List.of()).totalAmount(new BigDecimal("20.00"))
                .discountApplied(false).discountAmount(BigDecimal.ZERO)
                .status(PurchaseStatus.PAID).paymentMethod(PaymentMethod.CARD).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.status()).isEqualTo(PurchaseStatus.PAID);
        assertThat(dto.totalAmount()).isEqualByComparingTo("20.00");
        assertThat(dto.discountApplied()).isFalse();
    }

    // ── PaymentIntentResponse ─────────────────────────────────────────────────

    @Test
    void paymentIntentResponse_builderSetsAllFields() {
        PaymentIntentResponse dto = PaymentIntentResponse.builder()
                .clientSecret("cs_test").paymentIntentId("pi_123").publishableKey("pk_test").build();
        assertThat(dto.clientSecret()).isEqualTo("cs_test");
        assertThat(dto.paymentIntentId()).isEqualTo("pi_123");
        assertThat(dto.publishableKey()).isEqualTo("pk_test");
    }

    // ── PaymentHistoryResponse ────────────────────────────────────────────────

    @Test
    void paymentHistoryResponse_builderSetsKeyFields() {
        LocalDateTime now = LocalDateTime.now();
        PaymentHistoryResponse dto = PaymentHistoryResponse.builder()
                .purchaseId(1L).paymentIntentId("pi_xyz")
                .amount(new BigDecimal("25.00")).status(PurchaseStatus.PAID)
                .paymentMethod(PaymentMethod.CARD).type("purchase")
                .createdAt(now).userId(2L).userName("Carlos").build();
        assertThat(dto.purchaseId()).isEqualTo(1L);
        assertThat(dto.status()).isEqualTo(PurchaseStatus.PAID);
        assertThat(dto.userName()).isEqualTo("Carlos");
    }

    // ── RefundResponse ────────────────────────────────────────────────────────

    @Test
    void refundResponse_builderSetsAllFields() {
        RefundResponse dto = RefundResponse.builder()
                .refundId("re_123").amount(new BigDecimal("10.00")).status("succeeded").build();
        assertThat(dto.refundId()).isEqualTo("re_123");
        assertThat(dto.amount()).isEqualByComparingTo("10.00");
        assertThat(dto.status()).isEqualTo("succeeded");
    }

    // ── DashboardResponseDTO ──────────────────────────────────────────────────

    @Test
    void dashboardResponseDTO_builderSetsKeyFields() {
        DashboardResponseDTO dto = DashboardResponseDTO.builder()
                .totalRevenue(new BigDecimal("5000.00")).weeklyRevenue(new BigDecimal("1200.00"))
                .totalPurchases(200L).paidPurchases(180L)
                .activeScreenings(10L).totalUsers(500L)
                .activeMovies(15L).unresolvedIncidents(3L).build();
        assertThat(dto.totalRevenue()).isEqualByComparingTo("5000.00");
        assertThat(dto.totalPurchases()).isEqualTo(200L);
        assertThat(dto.activeMovies()).isEqualTo(15L);
    }

    // ── KpiResponseDTO ────────────────────────────────────────────────────────

    @Test
    void kpiResponseDTO_builderSetsAllFields() {
        KpiResponseDTO dto = KpiResponseDTO.builder()
                .revenueToday(new BigDecimal("350.00")).ticketsToday(35L)
                .occupancyAvg(72.5).incidentsOpen(2L)
                .activeSessions(8L).reservationsToday(12L).operationalRooms(5L).build();
        assertThat(dto.revenueToday()).isEqualByComparingTo("350.00");
        assertThat(dto.ticketsToday()).isEqualTo(35L);
        assertThat(dto.occupancyAvg()).isEqualTo(72.5);
    }

    // ── IncidentResponseDTO ───────────────────────────────────────────────────

    @Test
    void incidentResponseDTO_builderSetsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        IncidentResponseDTO dto = IncidentResponseDTO.builder()
                .id(1L).title("Light failure").description("Hall 2 lights out")
                .severity("HIGH").resolved(false).createdAt(now).updatedAt(now).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Light failure");
        assertThat(dto.severity()).isEqualTo("HIGH");
        assertThat(dto.resolved()).isFalse();
    }

    // ── MerchandiseResponseDTO ────────────────────────────────────────────────

    @Test
    void merchandiseResponseDTO_builderSetsKeyFields() {
        MerchandiseResponseDTO dto = MerchandiseResponseDTO.builder()
                .id(1L).name("Poster").category("POSTERS")
                .price(new BigDecimal("9.99")).stock(50).minStock(5).active(true).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Poster");
        assertThat(dto.stock()).isEqualTo(50);
        assertThat(dto.active()).isTrue();
    }

    // ── MerchandiseSaleResponseDTO ────────────────────────────────────────────

    @Test
    void merchandiseSaleResponseDTO_builderSetsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        MerchandiseSaleResponseDTO dto = MerchandiseSaleResponseDTO.builder()
                .id(1L).purchaseId(5L).userId(2L).merchandiseId(3L)
                .merchandiseName("Keychain").quantity(2)
                .total(new BigDecimal("7.98")).saleDate(now).build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.merchandiseName()).isEqualTo("Keychain");
        assertThat(dto.quantity()).isEqualTo(2);
        assertThat(dto.total()).isEqualByComparingTo("7.98");
    }

    // ── ShiftResponseDTO ──────────────────────────────────────────────────────

    @Test
    void shiftResponseDTO_builderSetsKeyFields() {
        ShiftResponseDTO dto = ShiftResponseDTO.builder()
                .id(1L).employeeId(2L).employeeName("Carlos")
                .employeeRole("CAJERO").shiftDate(LocalDate.now())
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(17, 0))
                .status("CONFIRMED").build();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.employeeName()).isEqualTo("Carlos");
        assertThat(dto.startTime()).isEqualTo(LocalTime.of(9, 0));
    }

    // ── OccupancyResponseDTO ──────────────────────────────────────────────────

    @Test
    void occupancyResponseDTO_builderSetsAllFields() {
        LocalDateTime start = LocalDateTime.of(2025, 9, 1, 18, 0);
        OccupancyResponseDTO dto = OccupancyResponseDTO.builder()
                .screeningId(1L).movieTitle("Dune").theaterName("Sala 1")
                .startTime(start).totalSeats(100).occupiedSeats(85)
                .occupancyPercentage(85.0).build();
        assertThat(dto.screeningId()).isEqualTo(1L);
        assertThat(dto.occupancyPercentage()).isEqualTo(85.0);
        assertThat(dto.occupiedSeats()).isEqualTo(85);
    }

    // ── SalesWeekResponseDTO ──────────────────────────────────────────────────

    @Test
    void salesWeekResponseDTO_builderSetsAllFields() {
        LocalDate date = LocalDate.of(2025, 6, 9);
        SalesWeekResponseDTO dto = SalesWeekResponseDTO.builder()
                .date(date).totalPurchases(42L).revenue(new BigDecimal("630.00")).build();
        assertThat(dto.date()).isEqualTo(date);
        assertThat(dto.totalPurchases()).isEqualTo(42L);
        assertThat(dto.revenue()).isEqualByComparingTo("630.00");
    }

    // ── YearlyDashboardResponseDTO ────────────────────────────────────────────

    @Test
    void yearlyDashboardResponseDTO_builderSetsKeyFields() {
        YearlyDashboardResponseDTO.TopMovieDTO topMovie = YearlyDashboardResponseDTO.TopMovieDTO.builder()
                .movieId(1L).movieTitle("Inception").revenue(new BigDecimal("12000.00")).build();
        YearlyDashboardResponseDTO.TopProductDTO topProduct = YearlyDashboardResponseDTO.TopProductDTO.builder()
                .productId(2L).productName("T-Shirt").revenue(new BigDecimal("3000.00")).build();
        YearlyDashboardResponseDTO dto = YearlyDashboardResponseDTO.builder()
                .year(2025).moviesProjected(120L).sessionsProjected(480L)
                .ticketRevenue(new BigDecimal("95000.00"))
                .merchandiseRevenue(new BigDecimal("18000.00"))
                .topMovies(List.of(topMovie)).topProducts(List.of(topProduct)).build();
        assertThat(dto.year()).isEqualTo(2025);
        assertThat(dto.topMovies()).hasSize(1);
        assertThat(dto.topMovies().get(0).movieTitle()).isEqualTo("Inception");
        assertThat(dto.topProducts().get(0).productName()).isEqualTo("T-Shirt");
    }

    // ── PayPurchaseResponseDTO ────────────────────────────────────────────────

    @Test
    void payPurchaseResponseDTO_builderSetsAllFields() {
        TicketQrDTO qr = TicketQrDTO.builder().ticketId(1L).qrCode("data:image/png;base64,abc").build();
        PayPurchaseResponseDTO dto = PayPurchaseResponseDTO.builder()
                .purchaseId(5L).status("PAID").tickets(List.of(qr)).paymentQrCode("qr_code").build();
        assertThat(dto.purchaseId()).isEqualTo(5L);
        assertThat(dto.status()).isEqualTo("PAID");
        assertThat(dto.tickets()).hasSize(1);
        assertThat(dto.tickets().get(0).ticketId()).isEqualTo(1L);
    }

    // ── TicketQrDTO ───────────────────────────────────────────────────────────

    @Test
    void ticketQrDTO_builderSetsAllFields() {
        TicketQrDTO dto = TicketQrDTO.builder().ticketId(10L).qrCode("base64data").build();
        assertThat(dto.ticketId()).isEqualTo(10L);
        assertThat(dto.qrCode()).isEqualTo("base64data");
    }

    // ── TicketOfficeResponseDTO ───────────────────────────────────────────────

    @Test
    void ticketOfficeResponseDTO_builderSetsAllFields() {
        TicketOfficeResponseDTO dto = TicketOfficeResponseDTO.builder()
                .saleId(1L).qrCodes(List.of("qr1", "qr2")).build();
        assertThat(dto.saleId()).isEqualTo(1L);
        assertThat(dto.qrCodes()).containsExactly("qr1", "qr2");
    }
}
