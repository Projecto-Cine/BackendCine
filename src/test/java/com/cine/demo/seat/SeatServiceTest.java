package com.cine.demo.seat;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.SeatMapper;
import com.cine.demo.model.Seat;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.SeatType;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.service.impl.SeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock private SeatRepository seatRepository;
    @Mock private TheaterRepository theaterRepository;
    @Mock private SeatMapper seatMapper;

    @InjectMocks
    private SeatServiceImpl seatService;

    private Theater theater;
    private Seat seat;

    @BeforeEach
    void setUp() {
        theater = Theater.builder().id(1L).name("Sala 1").capacity(50).build();
        seat = Seat.builder()
                .id(10L).theater(theater).row("A").number(1).type(SeatType.STANDARD).build();
    }

    @Test
    void getAll_returnsMappedSeats() {
        SeatResponseDTO dto = SeatResponseDTO.builder().id(10L).row("A").number(1).build();
        when(seatRepository.findAll()).thenReturn(List.of(seat));
        when(seatMapper.toResponseDto(seat)).thenReturn(dto);

        List<SeatResponseDTO> result = seatService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).row()).isEqualTo("A");
    }

    @Test
    void getByTheater_filtersByTheaterId() {
        when(seatRepository.findByTheaterId(1L)).thenReturn(List.of(seat));
        when(seatMapper.toResponseDto(seat))
                .thenReturn(SeatResponseDTO.builder().id(10L).build());

        List<SeatResponseDTO> result = seatService.getByTheater(1L);

        assertThat(result).hasSize(1);
        verify(seatRepository).findByTheaterId(1L);
    }

    @Test
    void getById_returnsSeat_whenFound() {
        when(seatRepository.findById(10L)).thenReturn(Optional.of(seat));
        when(seatMapper.toResponseDto(seat))
                .thenReturn(SeatResponseDTO.builder().id(10L).row("A").build());

        SeatResponseDTO result = seatService.getById(10L);

        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(seatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_throwsConflictException_whenSeatAlreadyExistsInTheater() {
        SeatRequestDTO dto = SeatRequestDTO.builder()
                .theaterId(1L).row("A").number(1).type("STANDARD").build();
        when(seatRepository.existsByTheaterIdAndRowAndNumber(1L, "A", 1)).thenReturn(true);

        assertThatThrownBy(() -> seatService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("A1");
    }

    @Test
    void create_throwsResourceNotFoundException_whenTheaterDoesNotExist() {
        SeatRequestDTO dto = SeatRequestDTO.builder()
                .theaterId(99L).row("A").number(1).type("STANDARD").build();
        when(seatRepository.existsByTheaterIdAndRowAndNumber(99L, "A", 1)).thenReturn(false);
        when(theaterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsSeat_whenValid() {
        SeatRequestDTO dto = SeatRequestDTO.builder()
                .theaterId(1L).row("B").number(2).type("VIP").build();
        when(seatRepository.existsByTheaterIdAndRowAndNumber(1L, "B", 2)).thenReturn(false);
        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> {
            Seat s = inv.getArgument(0);
            s.setId(20L);
            return s;
        });
        when(seatMapper.toResponseDto(any(Seat.class)))
                .thenReturn(SeatResponseDTO.builder().id(20L).row("B").number(2).type("VIP").build());

        SeatResponseDTO result = seatService.create(dto);

        assertThat(result.id()).isEqualTo(20L);
        verify(seatRepository).save(argThat(s ->
                s.getType() == SeatType.VIP && "B".equals(s.getRow()) && s.getNumber() == 2));
    }

    @Test
    void update_appliesPatchAndPersists() {
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder().type("VIP").build();
        when(seatRepository.findById(10L)).thenReturn(Optional.of(seat));
        when(seatRepository.save(seat)).thenReturn(seat);
        when(seatMapper.toResponseDto(seat))
                .thenReturn(SeatResponseDTO.builder().id(10L).build());

        seatService.update(10L, dto);

        verify(seatMapper).updateEntityFromDto(dto, seat);
        verify(seatRepository).save(seat);
    }

    @Test
    void update_throwsResourceNotFoundException_whenSeatNotFound() {
        when(seatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.update(99L,
                UpdateSeatRequestDTO.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesSeat_whenExists() {
        when(seatRepository.existsById(10L)).thenReturn(true);

        seatService.delete(10L);

        verify(seatRepository).deleteById(10L);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenSeatNotFound() {
        when(seatRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> seatService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(seatRepository, never()).deleteById(any());
    }
}