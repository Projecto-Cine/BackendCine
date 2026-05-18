package com.cine.demo.dto;

import com.cine.demo.dto.request.*;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.EmployeeRole;
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

    @Test
    void merchandiseSaleRequestDTO_invalid_whenQuantityZero() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder()
                .quantity(0).build();
        assertThat(hasViolationOn(validate(dto), "quantity")).isTrue();
    }

    @Test
    void merchandiseSaleRequestDTO_valid_whenQuantityIsOne() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder()
                .quantity(1).build();
        assertThat(hasViolationOn(validate(dto), "quantity")).isFalse();
    }

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
}
