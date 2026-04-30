package com.cine.demo.theater;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.TheaterMapper;
import com.cine.demo.model.Theater;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.service.impl.TheaterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TheaterServiceTest {

    @Mock private TheaterRepository theaterRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private TheaterMapper theaterMapper;

    @InjectMocks
    private TheaterServiceImpl theaterService;

    @Test
    void create_throwsConflictException_whenNameAlreadyExists() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder().nombre("Sala 1").capacidad(50).build();
        when(theaterRepository.existsByNombre("Sala 1")).thenReturn(true);

        assertThatThrownBy(() -> theaterService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Sala 1");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(theaterRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> theaterService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(theaterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> theaterService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
