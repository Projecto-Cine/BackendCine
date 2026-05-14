package com.cine.demo.service;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import java.util.List;

public interface ScreeningService {
    List<ScreeningResponseDTO> getAll();
    List<ScreeningResponseDTO> getUpcoming();
    ScreeningResponseDTO getById(Long id);
    List<ScreeningResponseDTO> getByMovie(Long movieId);
    ScreeningResponseDTO create(ScreeningRequestDTO dto);
    ScreeningResponseDTO update(Long id, UpdateScreeningRequestDTO dto);
    void delete(Long id);
    List<ScreeningSeatResponseDTO> getSeats(Long screeningId);
    ScreeningSeatResponseDTO tempReserveSeat(Long screeningId, Long seatId);
    ScreeningSeatResponseDTO reserveSeat(Long screeningId, Long seatId);
    ScreeningSeatResponseDTO releaseSeat(Long screeningId, Long seatId);
    void releaseExpiredReservations();
    List<ScreeningSeatResponseDTO> syncSeats(Long screeningId);
}
