package com.cine.demo.dto;

import com.cine.demo.dto.request.*;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.EmployeeRole;
import com.cine.demo.model.enums.TicketType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private <T> Set<ConstraintViolation<T>> validate(T dto) {
        return validator.validate(dto);
    }

    private <T> boolean hasViolationOn(Set<ConstraintViolation<T>> violations, String field) {
        return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    // ── UserRequestDTO ────────────────────────────────────────────────────────

    @Test
    void userRequestDTO_valid_whenAllFieldsCorrect() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana").email("ana@test.com").password("secret")
                .birthDate(LocalDate.of(1990, 1, 1)).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void userRequestDTO_invalid_whenNameBlank() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("").email("ana@test.com").password("secret")
                .birthDate(LocalDate.of(1990, 1, 1)).build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void userRequestDTO_invalid_whenNameTooShort() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("A").email("ana@test.com").password("secret")
                .birthDate(LocalDate.of(1990, 1, 1)).build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void userRequestDTO_invalid_whenEmailMalformed() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana").email("not-an-email").password("secret")
                .birthDate(LocalDate.of(1990, 1, 1)).build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    @Test
    void userRequestDTO_invalid_whenPasswordBlank() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana").email("ana@test.com").password("")
                .birthDate(LocalDate.of(1990, 1, 1)).build();
        assertThat(hasViolationOn(validate(dto), "password")).isTrue();
    }

    @Test
    void userRequestDTO_invalid_whenBirthDateNull() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana").email("ana@test.com").password("secret")
                .birthDate(null).build();
        assertThat(hasViolationOn(validate(dto), "birthDate")).isTrue();
    }

    // ── LoginRequestDTO ───────────────────────────────────────────────────────

    @Test
    void loginRequestDTO_valid_whenAllFieldsCorrect() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("user@test.com").password("pass").build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void loginRequestDTO_invalid_whenEmailBlank() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("").password("pass").build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    @Test
    void loginRequestDTO_invalid_whenEmailMalformed() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("badformat").password("pass").build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    @Test
    void loginRequestDTO_invalid_whenPasswordBlank() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("user@test.com").password("").build();
        assertThat(hasViolationOn(validate(dto), "password")).isTrue();
    }

    // ── RegisterRequestDTO ────────────────────────────────────────────────────

    @Test
    void registerRequestDTO_valid_whenAllFieldsCorrect() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .name("Ana").email("ana@test.com").password("secret")
                .birthDate(LocalDate.of(1995, 5, 10)).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void registerRequestDTO_invalid_whenNameTooShort() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .name("A").email("ana@test.com").password("secret")
                .birthDate(LocalDate.of(1995, 5, 10)).build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void registerRequestDTO_invalid_whenEmailMalformed() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .name("Ana").email("bad").password("secret")
                .birthDate(LocalDate.of(1995, 5, 10)).build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    @Test
    void registerRequestDTO_invalid_whenBirthDateNull() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .name("Ana").email("ana@test.com").password("secret")
                .birthDate(null).build();
        assertThat(hasViolationOn(validate(dto), "birthDate")).isTrue();
    }

    // ── QuickRegisterDTO ──────────────────────────────────────────────────────

    @Test
    void quickRegisterDTO_valid_whenAllFieldsCorrect() {
        QuickRegisterDTO dto = QuickRegisterDTO.builder()
                .name("Ana").lastName("García").email("ana@test.com").password("secret").build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void quickRegisterDTO_invalid_whenNameBlank() {
        QuickRegisterDTO dto = QuickRegisterDTO.builder()
                .name("").lastName("García").email("ana@test.com").password("secret").build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void quickRegisterDTO_invalid_whenLastNameBlank() {
        QuickRegisterDTO dto = QuickRegisterDTO.builder()
                .name("Ana").lastName("").email("ana@test.com").password("secret").build();
        assertThat(hasViolationOn(validate(dto), "lastName")).isTrue();
    }

    @Test
    void quickRegisterDTO_invalid_whenEmailMalformed() {
        QuickRegisterDTO dto = QuickRegisterDTO.builder()
                .name("Ana").lastName("García").email("bad-email").password("secret").build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    @Test
    void quickRegisterDTO_invalid_whenPasswordBlank() {
        QuickRegisterDTO dto = QuickRegisterDTO.builder()
                .name("Ana").lastName("García").email("ana@test.com").password("").build();
        assertThat(hasViolationOn(validate(dto), "password")).isTrue();
    }

    // ── ClientUpdateRequestDTO ────────────────────────────────────────────────

    @Test
    void clientUpdateRequestDTO_valid_whenAllFieldsNull() {
        ClientUpdateRequestDTO dto = ClientUpdateRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void clientUpdateRequestDTO_invalid_whenNameTooShort() {
        ClientUpdateRequestDTO dto = ClientUpdateRequestDTO.builder().name("X").build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void clientUpdateRequestDTO_invalid_whenEmailMalformed() {
        ClientUpdateRequestDTO dto = ClientUpdateRequestDTO.builder().email("not-valid").build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    // ── UpdateUserRequestDTO ──────────────────────────────────────────────────

    @Test
    void updateUserRequestDTO_valid_whenAllFieldsNull() {
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void updateUserRequestDTO_invalid_whenNameTooShort() {
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().name("X").build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void updateUserRequestDTO_invalid_whenEmailMalformed() {
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().email("nope").build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    // ── MovieRequestDTO ───────────────────────────────────────────────────────

    @Test
    void movieRequestDTO_valid_whenAllFieldsCorrect() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Inception").genre("Sci-Fi").durationMin(148)
                .ageRating(AgeRating.TWELVE).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void movieRequestDTO_invalid_whenTitleBlank() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("").genre("Sci-Fi").durationMin(100).ageRating(AgeRating.ALL).build();
        assertThat(hasViolationOn(validate(dto), "title")).isTrue();
    }

    @Test
    void movieRequestDTO_invalid_whenDurationZero() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Movie").genre("Action").durationMin(0).ageRating(AgeRating.ALL).build();
        assertThat(hasViolationOn(validate(dto), "durationMin")).isTrue();
    }

    @Test
    void movieRequestDTO_invalid_whenAgeRatingNull() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Movie").genre("Action").durationMin(90).ageRating(null).build();
        assertThat(hasViolationOn(validate(dto), "ageRating")).isTrue();
    }

    // ── UpdateMovieRequestDTO ─────────────────────────────────────────────────

    @Test
    void updateMovieRequestDTO_valid_whenAllFieldsNull() {
        UpdateMovieRequestDTO dto = UpdateMovieRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void updateMovieRequestDTO_invalid_whenDurationZero() {
        UpdateMovieRequestDTO dto = UpdateMovieRequestDTO.builder().durationMin(0).build();
        assertThat(hasViolationOn(validate(dto), "durationMin")).isTrue();
    }

    // ── ScreeningRequestDTO ───────────────────────────────────────────────────

    @Test
    void screeningRequestDTO_valid_whenAllFieldsCorrect() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(new BigDecimal("10.00")).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void screeningRequestDTO_invalid_whenMovieIdNull() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(null).theaterId(1L)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(BigDecimal.TEN).build();
        assertThat(hasViolationOn(validate(dto), "movieId")).isTrue();
    }

    @Test
    void screeningRequestDTO_invalid_whenStartTimeInPast() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .startTime(LocalDateTime.now().minusDays(1))
                .basePrice(BigDecimal.TEN).build();
        assertThat(hasViolationOn(validate(dto), "startTime")).isTrue();
    }

    @Test
    void screeningRequestDTO_invalid_whenBasePriceNull() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(null).build();
        assertThat(hasViolationOn(validate(dto), "basePrice")).isTrue();
    }

    // ── UpdateScreeningRequestDTO ─────────────────────────────────────────────

    @Test
    void updateScreeningRequestDTO_valid_whenAllFieldsNull() {
        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void updateScreeningRequestDTO_invalid_whenStartTimeInPast() {
        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder()
                .startTime(LocalDateTime.now().minusDays(1)).build();
        assertThat(hasViolationOn(validate(dto), "startTime")).isTrue();
    }

    @Test
    void updateScreeningRequestDTO_invalid_whenBasePriceNegative() {
        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder()
                .basePrice(new BigDecimal("-1.00")).build();
        assertThat(hasViolationOn(validate(dto), "basePrice")).isTrue();
    }

    // ── TheaterRequestDTO ─────────────────────────────────────────────────────

    @Test
    void theaterRequestDTO_valid_whenAllFieldsCorrect() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder().name("Sala 1").capacity(50).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void theaterRequestDTO_invalid_whenNameBlank() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder().name("").capacity(50).build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void theaterRequestDTO_invalid_whenCapacityZero() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder().name("Sala 1").capacity(0).build();
        assertThat(hasViolationOn(validate(dto), "capacity")).isTrue();
    }

    // ── UpdateTheaterRequestDTO ───────────────────────────────────────────────

    @Test
    void updateTheaterRequestDTO_valid_whenAllFieldsNull() {
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void updateTheaterRequestDTO_invalid_whenCapacityZero() {
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder().capacity(0).build();
        assertThat(hasViolationOn(validate(dto), "capacity")).isTrue();
    }

    // ── EmployeeRequestDTO ────────────────────────────────────────────────────

    @Test
    void employeeRequestDTO_valid_whenAllFieldsCorrect() {
        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .name("Carlos").email("carlos@cine.com").password("secret")
                .role(EmployeeRole.CASHIER).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void employeeRequestDTO_invalid_whenRoleNull() {
        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .name("Carlos").email("carlos@cine.com").password("secret")
                .role(null).build();
        assertThat(hasViolationOn(validate(dto), "role")).isTrue();
    }

    @Test
    void employeeRequestDTO_invalid_whenEmailMalformed() {
        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .name("Carlos").email("not-email").password("secret")
                .role(EmployeeRole.CASHIER).build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    // ── UpdateEmployeeRequestDTO ──────────────────────────────────────────────

    @Test
    void updateEmployeeRequestDTO_valid_whenAllFieldsNull() {
        UpdateEmployeeRequestDTO dto = UpdateEmployeeRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void updateEmployeeRequestDTO_invalid_whenEmailMalformed() {
        UpdateEmployeeRequestDTO dto = UpdateEmployeeRequestDTO.builder().email("bad").build();
        assertThat(hasViolationOn(validate(dto), "email")).isTrue();
    }

    // ── ShiftRequestDTO ───────────────────────────────────────────────────────

    @Test
    void shiftRequestDTO_valid_whenAllFieldsCorrect() {
        ShiftRequestDTO dto = ShiftRequestDTO.builder()
                .employeeId(1L).shiftDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(17, 0)).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void shiftRequestDTO_invalid_whenEmployeeIdNull() {
        ShiftRequestDTO dto = ShiftRequestDTO.builder()
                .employeeId(null).shiftDate(LocalDate.now())
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(17, 0)).build();
        assertThat(hasViolationOn(validate(dto), "employeeId")).isTrue();
    }

    @Test
    void shiftRequestDTO_invalid_whenShiftDateNull() {
        ShiftRequestDTO dto = ShiftRequestDTO.builder()
                .employeeId(1L).shiftDate(null)
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(17, 0)).build();
        assertThat(hasViolationOn(validate(dto), "shiftDate")).isTrue();
    }

    // ── SeatRequestDTO ────────────────────────────────────────────────────────

    @Test
    void seatRequestDTO_valid_whenAllFieldsCorrect() {
        SeatRequestDTO dto = SeatRequestDTO.builder()
                .theaterId(1L).row("A").number(5).type("STANDARD").build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void seatRequestDTO_invalid_whenRowBlank() {
        SeatRequestDTO dto = SeatRequestDTO.builder()
                .theaterId(1L).row("").number(5).type("STANDARD").build();
        assertThat(hasViolationOn(validate(dto), "row")).isTrue();
    }

    @Test
    void seatRequestDTO_invalid_whenNumberZero() {
        SeatRequestDTO dto = SeatRequestDTO.builder()
                .theaterId(1L).row("A").number(0).type("STANDARD").build();
        assertThat(hasViolationOn(validate(dto), "number")).isTrue();
    }

    // ── UpdateSeatRequestDTO ──────────────────────────────────────────────────

    @Test
    void updateSeatRequestDTO_valid_whenAllFieldsNull() {
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder().build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void updateSeatRequestDTO_invalid_whenNumberZero() {
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder().number(0).build();
        assertThat(hasViolationOn(validate(dto), "number")).isTrue();
    }

    // ── IncidentRequestDTO ────────────────────────────────────────────────────

    @Test
    void incidentRequestDTO_valid_whenAllFieldsCorrect() {
        IncidentRequestDTO dto = IncidentRequestDTO.builder()
                .title("Light out").severity("HIGH").build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void incidentRequestDTO_invalid_whenTitleBlank() {
        IncidentRequestDTO dto = IncidentRequestDTO.builder()
                .title("").severity("LOW").build();
        assertThat(hasViolationOn(validate(dto), "title")).isTrue();
    }

    @Test
    void incidentRequestDTO_invalid_whenSeverityBlank() {
        IncidentRequestDTO dto = IncidentRequestDTO.builder()
                .title("Light out").severity("").build();
        assertThat(hasViolationOn(validate(dto), "severity")).isTrue();
    }

    // ── MerchandiseRequestDTO ─────────────────────────────────────────────────

    @Test
    void merchandiseRequestDTO_valid_whenAllFieldsCorrect() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("T-Shirt").price(new BigDecimal("15.00")).stock(10).minStock(2).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void merchandiseRequestDTO_invalid_whenNameBlank() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("").price(BigDecimal.TEN).build();
        assertThat(hasViolationOn(validate(dto), "name")).isTrue();
    }

    @Test
    void merchandiseRequestDTO_invalid_whenPriceNull() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("T-Shirt").price(null).build();
        assertThat(hasViolationOn(validate(dto), "price")).isTrue();
    }

    @Test
    void merchandiseRequestDTO_invalid_whenStockNegative() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("T-Shirt").price(BigDecimal.TEN).stock(-1).build();
        assertThat(hasViolationOn(validate(dto), "stock")).isTrue();
    }

    // ── MerchandiseSaleRequestDTO ─────────────────────────────────────────────

    @Test
    void merchandiseSaleRequestDTO_invalid_whenQuantityZero() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder().quantity(0).build();
        assertThat(hasViolationOn(validate(dto), "quantity")).isTrue();
    }

    @Test
    void merchandiseSaleRequestDTO_valid_whenQuantityIsOne() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder().quantity(1).build();
        assertThat(hasViolationOn(validate(dto), "quantity")).isFalse();
    }

    // ── PurchaseRequestDTO ────────────────────────────────────────────────────

    @Test
    void purchaseRequestDTO_valid_whenTicketsPresent() {
        TicketRequestDTO ticket = TicketRequestDTO.builder()
                .screeningSeatId(1L).ticketType(TicketType.ADULT).build();
        PurchaseRequestDTO dto = PurchaseRequestDTO.builder()
                .tickets(List.of(ticket)).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void purchaseRequestDTO_invalid_whenTicketsNull() {
        PurchaseRequestDTO dto = PurchaseRequestDTO.builder().tickets(null).build();
        assertThat(hasViolationOn(validate(dto), "tickets")).isTrue();
    }

    @Test
    void purchaseRequestDTO_invalid_whenGuestEmailMalformed() {
        TicketRequestDTO ticket = TicketRequestDTO.builder()
                .screeningSeatId(1L).ticketType(TicketType.ADULT).build();
        PurchaseRequestDTO dto = PurchaseRequestDTO.builder()
                .tickets(List.of(ticket)).guestEmail("not-an-email").build();
        assertThat(hasViolationOn(validate(dto), "guestEmail")).isTrue();
    }

    // ── TicketRequestDTO ──────────────────────────────────────────────────────

    @Test
    void ticketRequestDTO_valid_whenAllFieldsCorrect() {
        TicketRequestDTO dto = TicketRequestDTO.builder()
                .screeningSeatId(1L).ticketType(TicketType.ADULT).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void ticketRequestDTO_invalid_whenScreeningSeatIdNull() {
        TicketRequestDTO dto = TicketRequestDTO.builder()
                .screeningSeatId(null).ticketType(TicketType.ADULT).build();
        assertThat(hasViolationOn(validate(dto), "screeningSeatId")).isTrue();
    }

    @Test
    void ticketRequestDTO_invalid_whenTicketTypeNull() {
        TicketRequestDTO dto = TicketRequestDTO.builder()
                .screeningSeatId(1L).ticketType(null).build();
        assertThat(hasViolationOn(validate(dto), "ticketType")).isTrue();
    }

    // ── TicketOfficeRequestDTO ────────────────────────────────────────────────

    @Test
    void ticketOfficeRequestDTO_valid_whenAllRequiredFieldsPresent() {
        TicketOfficeRequestDTO dto = TicketOfficeRequestDTO.builder()
                .screeningId(1L).seats(List.of("A1")).ticketType("ADULT")
                .unitPrice(BigDecimal.TEN).total(BigDecimal.TEN)
                .paymentMethod("CASH").cashierId(2L).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void ticketOfficeRequestDTO_invalid_whenScreeningIdNull() {
        TicketOfficeRequestDTO dto = TicketOfficeRequestDTO.builder()
                .screeningId(null).seats(List.of("A1")).ticketType("ADULT")
                .unitPrice(BigDecimal.TEN).total(BigDecimal.TEN)
                .paymentMethod("CASH").cashierId(2L).build();
        assertThat(hasViolationOn(validate(dto), "screeningId")).isTrue();
    }

    @Test
    void ticketOfficeRequestDTO_invalid_whenSeatsEmpty() {
        TicketOfficeRequestDTO dto = TicketOfficeRequestDTO.builder()
                .screeningId(1L).seats(List.of()).ticketType("ADULT")
                .unitPrice(BigDecimal.TEN).total(BigDecimal.TEN)
                .paymentMethod("CASH").cashierId(2L).build();
        assertThat(hasViolationOn(validate(dto), "seats")).isTrue();
    }

    // ── RefundRequest ─────────────────────────────────────────────────────────

    @Test
    void refundRequest_valid_whenPurchaseIdPresent() {
        RefundRequest dto = RefundRequest.builder().purchaseId(1L).build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void refundRequest_invalid_whenPurchaseIdNull() {
        RefundRequest dto = RefundRequest.builder().purchaseId(null).build();
        assertThat(hasViolationOn(validate(dto), "purchaseId")).isTrue();
    }

    // ── CreatePaymentIntentRequest ────────────────────────────────────────────

    @Test
    void createPaymentIntentRequest_valid_whenAllFieldsCorrect() {
        CreatePaymentIntentRequest dto = CreatePaymentIntentRequest.builder()
                .purchaseId(1L).amount(new BigDecimal("20.00")).currency("EUR").build();
        assertThat(validate(dto)).isEmpty();
    }

    @Test
    void createPaymentIntentRequest_invalid_whenPurchaseIdNull() {
        CreatePaymentIntentRequest dto = CreatePaymentIntentRequest.builder()
                .purchaseId(null).amount(BigDecimal.TEN).currency("EUR").build();
        assertThat(hasViolationOn(validate(dto), "purchaseId")).isTrue();
    }

    @Test
    void createPaymentIntentRequest_invalid_whenAmountNotPositive() {
        CreatePaymentIntentRequest dto = CreatePaymentIntentRequest.builder()
                .purchaseId(1L).amount(BigDecimal.ZERO).currency("EUR").build();
        assertThat(hasViolationOn(validate(dto), "amount")).isTrue();
    }

    // ── PayPurchaseRequestDTO ─────────────────────────────────────────────────

    @Test
    void payPurchaseRequestDTO_buildsCorrectly() {
        PayPurchaseRequestDTO dto = PayPurchaseRequestDTO.builder()
                .paymentMethod("CARD").cardLastFour("1234").build();
        assertThat(dto.paymentMethod()).isEqualTo("CARD");
        assertThat(dto.cardLastFour()).isEqualTo("1234");
    }
}
