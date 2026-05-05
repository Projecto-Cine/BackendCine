package com.cine.demo.purchase;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.exception.*;
import com.cine.demo.mapper.PurchaseMapper;
import com.cine.demo.model.*;
import com.cine.demo.model.enums.*;
import com.cine.demo.repository.*;
import com.cine.demo.service.ScreeningService;
import com.cine.demo.service.impl.PurchaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock private PurchaseRepository purchaseRepository;
    @Mock private UserRepository userRepository;
    @Mock private ScreeningRepository screeningRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private ScreeningSeatRepository screeningSeatRepository;
    @Mock private PurchaseMapper purchaseMapper;
    @Mock private ScreeningService screeningService;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private User user;
    private Movie movie;
    private Theater theater;
    private Screening screening;
    private Seat seat;
    private ScreeningSeat screeningSeat;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L).title("Test Movie").durationMin(120)
                .genre("Action")
                .ageRating(AgeRating.ALL).build();

        theater = Theater.builder().id(1L).nombre("Sala 1").capacidad(50).build();

        screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .precioBase(BigDecimal.TEN)
                .asientosDisponibles(10)
                .build();

        seat = Seat.builder()
                .id(1L).theater(theater).fila("A").numero(1).tipo(SeatType.STANDARD).build();

        screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).seat(seat).ocupado(false).build();

        user = User.builder()
                .id(1L).nombre("Ana").email("ana@test.com").password("pass")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .visitasAnio(0).rol(Role.CLIENTE).build();
    }

    private PurchaseRequestDTO buildRequest(TicketType ticketType) {
        return PurchaseRequestDTO.builder()
                .userId(1L).screeningId(1L)
                .tickets(List.of(TicketRequestDTO.builder().seatId(1L).ticketType(ticketType).build()))
                .build();
    }

    @Test
    void create_throwsScreeningAlreadyPassedException_whenScreeningInPast() {
        screening.setFechaHora(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> purchaseService.create(buildRequest(TicketType.ADULT)))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    @Test
    void create_throwsSeatAlreadyTakenException_whenSeatOccupied() {
        screeningSeat.setOcupado(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));

        assertThatThrownBy(() -> purchaseService.create(buildRequest(TicketType.ADULT)))
                .isInstanceOf(SeatAlreadyTakenException.class);
    }

    @Test
    void create_throwsMinorWithoutAdultException_whenChildWithoutAdult() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        PurchaseRequestDTO dto = PurchaseRequestDTO.builder()
                .userId(1L).screeningId(1L)
                .tickets(List.of(TicketRequestDTO.builder().seatId(1L).ticketType(TicketType.CHILD).build()))
                .build();

        assertThatThrownBy(() -> purchaseService.create(dto))
                .isInstanceOf(MinorWithoutAdultException.class);
    }

    @Test
    void create_throwsAgeRestrictionException_whenUserTooYoung() {
        movie.setAgeRating(AgeRating.EIGHTEEN);
        user.setFechaNacimiento(LocalDate.now().minusYears(15));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> purchaseService.create(buildRequest(TicketType.ADULT)))
                .isInstanceOf(AgeRestrictionException.class);
    }

    @Test
    void create_appliesFidelityDiscountOnAdultTickets_whenVisitasOver10() {
        user.setVisitasAnio(11);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningService.reserveSeat(anyLong(), anyLong())).thenReturn(mock(ScreeningSeatResponseDTO.class));
        when(purchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseMapper.toResponseDto(any())).thenReturn(null);

        purchaseService.create(buildRequest(TicketType.ADULT));

        verify(purchaseRepository).save(argThat(purchase ->
                purchase.isDiscountApplied() &&
                purchase.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0
        ));
    }

    @Test
    void create_doesNotApplyDiscount_whenVisitasLessOrEqual10() {
        user.setVisitasAnio(5);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningService.reserveSeat(anyLong(), anyLong())).thenReturn(mock(ScreeningSeatResponseDTO.class));
        when(purchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseMapper.toResponseDto(any())).thenReturn(null);

        purchaseService.create(buildRequest(TicketType.ADULT));

        verify(purchaseRepository).save(argThat(purchase ->
                !purchase.isDiscountApplied() &&
                purchase.getDiscountAmount().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void confirm_throwsInvalidPurchaseStatusException_whenNotPending() {
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PAID).totalAmount(BigDecimal.TEN).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> purchaseService.confirm(1L))
                .isInstanceOf(InvalidPurchaseStatusException.class);
    }

    @Test
    void confirm_incrementsUserVisitasAnio() {
        user.setVisitasAnio(3);
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(userRepository.save(any())).thenReturn(user);
        when(purchaseRepository.save(any())).thenReturn(purchase);
        when(purchaseMapper.toResponseDto(any())).thenReturn(null);

        purchaseService.confirm(1L);

        assertThat(user.getVisitasAnio()).isEqualTo(4);
        verify(userRepository).save(user);
    }

    @Test
    void cancel_throwsPurchaseAlreadyCancelledException_whenAlreadyCancelled() {
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.CANCELLED).totalAmount(BigDecimal.TEN).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> purchaseService.cancel(1L))
                .isInstanceOf(PurchaseAlreadyCancelledException.class);
    }

    @Test
    void cancel_releasesSeatForEachTicket() {
        Ticket ticket = Ticket.builder()
                .id(1L).seat(seat).screening(screening)
                .ticketType(TicketType.ADULT).unitPrice(BigDecimal.TEN).build();
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN)
                .tickets(List.of(ticket)).build();
        ticket.setPurchase(purchase);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(screeningService.releaseSeat(anyLong(), anyLong())).thenReturn(mock(ScreeningSeatResponseDTO.class));
        when(purchaseRepository.save(any())).thenReturn(purchase);
        when(purchaseMapper.toResponseDto(any())).thenReturn(null);

        purchaseService.cancel(1L);

        verify(screeningService).releaseSeat(screening.getId(), seat.getId());
        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CANCELLED);
    }

    /**
     * create con usuario inexistente: lanza ResourceNotFoundException
     * antes de tocar la proyección.
     */
    @Test
    void create_throwsResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        PurchaseRequestDTO dto = PurchaseRequestDTO.builder()
                .userId(99L).screeningId(1L)
                .tickets(List.of(TicketRequestDTO.builder().seatId(1L).ticketType(TicketType.ADULT).build()))
                .build();

        assertThatThrownBy(() -> purchaseService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * create con proyección inexistente: lanza ResourceNotFoundException.
     */
    @Test
    void create_throwsResourceNotFoundException_whenScreeningNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(99L)).thenReturn(Optional.empty());

        PurchaseRequestDTO dto = PurchaseRequestDTO.builder()
                .userId(1L).screeningId(99L)
                .tickets(List.of(TicketRequestDTO.builder().seatId(1L).ticketType(TicketType.ADULT).build()))
                .build();

        assertThatThrownBy(() -> purchaseService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * create con asiento que NO pertenece a la sala de esta proyección:
     * lanza ConflictException. Esto evita que un usuario reserve un
     * asiento de otra sala accidentalmente.
     */
    @Test
    void create_throwsConflictException_whenSeatBelongsToAnotherTheater() {
        Theater otherTheater = Theater.builder().id(99L).nombre("Otra sala").build();
        Seat foreignSeat = Seat.builder()
                .id(2L).theater(otherTheater).fila("A").numero(1).tipo(SeatType.STANDARD).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(seatRepository.findById(2L)).thenReturn(Optional.of(foreignSeat));

        PurchaseRequestDTO dto = PurchaseRequestDTO.builder()
                .userId(1L).screeningId(1L)
                .tickets(List.of(TicketRequestDTO.builder().seatId(2L).ticketType(TicketType.ADULT).build()))
                .build();

        assertThatThrownBy(() -> purchaseService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no pertenece");
    }

    /**
     * Cubre ramas de validación de edad para distintos rangos:
     * SEVEN — usuario de 5 años → AgeRestrictionException.
     */
    @Test
    void create_throwsAgeRestrictionException_whenSevenAndUserUnderSeven() {
        movie.setAgeRating(AgeRating.SEVEN);
        user.setFechaNacimiento(LocalDate.now().minusYears(5));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> purchaseService.create(buildRequest(TicketType.ADULT)))
                .isInstanceOf(AgeRestrictionException.class)
                .hasMessageContaining("7");
    }

    /**
     * SIXTEEN — usuario de 14 años → AgeRestrictionException.
     */
    @Test
    void create_throwsAgeRestrictionException_whenSixteenAndUserUnderSixteen() {
        movie.setAgeRating(AgeRating.SIXTEEN);
        user.setFechaNacimiento(LocalDate.now().minusYears(14));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> purchaseService.create(buildRequest(TicketType.ADULT)))
                .isInstanceOf(AgeRestrictionException.class)
                .hasMessageContaining("16");
    }

    /**
     * TWELVE — usuario de 10 años → AgeRestrictionException.
     */
    @Test
    void create_throwsAgeRestrictionException_whenTwelveAndUserUnderTwelve() {
        movie.setAgeRating(AgeRating.TWELVE);
        user.setFechaNacimiento(LocalDate.now().minusYears(10));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> purchaseService.create(buildRequest(TicketType.ADULT)))
                .isInstanceOf(AgeRestrictionException.class)
                .hasMessageContaining("12");
    }

    /**
     * AgeRating.ALL no debe lanzar ninguna restricción de edad,
     * sea cual sea la edad del usuario. Caso "happy path" que valida
     * que el guard "if (rating == ALL) return" funciona.
     */
    @Test
    void create_doesNotThrowAgeRestriction_whenAgeRatingIsAll() {
        movie.setAgeRating(AgeRating.ALL);
        user.setFechaNacimiento(LocalDate.now().minusYears(3));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningService.reserveSeat(anyLong(), anyLong())).thenReturn(mock(ScreeningSeatResponseDTO.class));
        when(purchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseMapper.toResponseDto(any())).thenReturn(null);

        // Para que no salte MinorWithoutAdult al ser CHILD, usamos ADULT
        purchaseService.create(buildRequest(TicketType.ADULT));

        verify(purchaseRepository).save(any());
    }

    /**
     * cancel cuando la proyección ya pasó: lanza ScreeningAlreadyPassedException.
     * Reglas de negocio: no se puede cancelar una compra de una sesión vencida.
     */
    @Test
    void cancel_throwsScreeningAlreadyPassedException_whenScreeningAlreadyPassed() {
        Screening pastScreening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().minusDays(1))
                .precioBase(BigDecimal.TEN).build();
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(pastScreening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> purchaseService.cancel(1L))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    /**
     * confirm caso feliz: estado pasa a PAID y se incrementa visitasAnio.
     */
    @Test
    void confirm_setsStatusToPaid_whenPending() {
        user.setVisitasAnio(7);
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(userRepository.save(user)).thenReturn(user);
        when(purchaseRepository.save(any())).thenReturn(purchase);
        when(purchaseMapper.toResponseDto(any())).thenReturn(null);

        purchaseService.confirm(1L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PAID);
        assertThat(user.getVisitasAnio()).isEqualTo(8);
    }

    /**
     * getById con id inexistente: ResourceNotFoundException.
     */
    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(purchaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * getById caso feliz devuelve el DTO de la compra.
     */
    @Test
    void getById_returnsPurchaseDto_whenFound() {
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(purchaseMapper.toResponseDto(purchase))
                .thenReturn(com.cine.demo.dto.response.PurchaseResponseDTO.builder().id(1L).build());

        var result = purchaseService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    /**
     * getByUser delega en findByUserId.
     */
    @Test
    void getByUser_returnsListMappedToDto() {
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseRepository.findByUserId(1L)).thenReturn(List.of(purchase));
        when(purchaseMapper.toResponseDto(purchase))
                .thenReturn(com.cine.demo.dto.response.PurchaseResponseDTO.builder().id(1L).build());

        var result = purchaseService.getByUser(1L);

        assertThat(result).hasSize(1);
        verify(purchaseRepository).findByUserId(1L);
    }

    /**
     * getByScreening delega en findByScreeningId.
     */
    @Test
    void getByScreening_returnsListMappedToDto() {
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .status(PurchaseStatus.PENDING).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseRepository.findByScreeningId(1L)).thenReturn(List.of(purchase));
        when(purchaseMapper.toResponseDto(purchase))
                .thenReturn(com.cine.demo.dto.response.PurchaseResponseDTO.builder().id(1L).build());

        var result = purchaseService.getByScreening(1L);

        assertThat(result).hasSize(1);
        verify(purchaseRepository).findByScreeningId(1L);
    }
}
