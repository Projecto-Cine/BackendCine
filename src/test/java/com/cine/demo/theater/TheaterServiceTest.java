package com.cine.demo.theater;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.TheaterMapper;
import com.cine.demo.model.Seat;
import com.cine.demo.model.Theater;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.service.impl.TheaterServiceImpl;
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
class TheaterServiceTest {

    @Mock private TheaterRepository theaterRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private TheaterMapper theaterMapper;

    @InjectMocks
    private TheaterServiceImpl theaterService;

    @Test
    void getAll_returnsListOfTheaters() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(50).build();
        TheaterResponseDTO dto = TheaterResponseDTO.builder().id(1L).name("Sala 1").build();
        when(theaterRepository.findAll()).thenReturn(List.of(theater));
        when(theaterMapper.toResponseDto(theater)).thenReturn(dto);

        List<TheaterResponseDTO> result = theaterService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sala 1");
    }

    @Test
    void getById_returnsTheater_whenFound() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(50).build();
        TheaterResponseDTO dto = TheaterResponseDTO.builder().id(1L).name("Sala 1").build();
        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(theaterMapper.toResponseDto(theater)).thenReturn(dto);

        TheaterResponseDTO result = theaterService.getById(1L);

        assertThat(result.getName()).isEqualTo("Sala 1");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(theaterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> theaterService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_throwsConflictException_whenNameAlreadyExists() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder().name("Sala 1").capacity(50).build();
        when(theaterRepository.existsByName("Sala 1")).thenReturn(true);

        assertThatThrownBy(() -> theaterService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Sala 1");
    }

    @Test
    void create_savesTheaterAndGeneratesSeats_whenNameNew() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder().name("Sala 2").capacity(15).build();
        Theater entity = Theater.builder().name("Sala 2").capacity(15).build();
        Theater saved = Theater.builder().id(2L).name("Sala 2").capacity(15).build();
        when(theaterRepository.existsByName("Sala 2")).thenReturn(false);
        when(theaterMapper.toEntity(dto)).thenReturn(entity);
        when(theaterRepository.save(entity)).thenReturn(saved);
        when(theaterMapper.toResponseDto(saved))
                .thenReturn(TheaterResponseDTO.builder().id(2L).name("Sala 2").build());

        TheaterResponseDTO result = theaterService.create(dto);

        assertThat(result.getId()).isEqualTo(2L);
        verify(seatRepository, times(15)).save(any(Seat.class));
    }

    @Test
    void update_updatesAndReturnsTheater_whenFound() {
        Theater existing = Theater.builder().id(1L).name("Sala 1").capacity(50).build();
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder().name("Sala Renombrada").build();
        when(theaterRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(theaterRepository.save(existing)).thenReturn(existing);
        when(theaterMapper.toResponseDto(existing))
                .thenReturn(TheaterResponseDTO.builder().id(1L).name("Sala Renombrada").build());

        TheaterResponseDTO result = theaterService.update(1L, dto);

        verify(theaterMapper).updateEntityFromDto(dto, existing);
        assertThat(result.getName()).isEqualTo("Sala Renombrada");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(theaterRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> theaterService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_callsRepository_whenExists() {
        when(theaterRepository.existsById(1L)).thenReturn(true);

        theaterService.delete(1L);

        verify(theaterRepository).deleteById(1L);
    }
}
