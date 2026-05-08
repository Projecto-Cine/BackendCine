package com.cine.demo.service;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import java.util.List;

public interface SeatService {
    List<SeatResponseDTO> getAll();
    List<SeatResponseDTO> getByTheater(Long theaterId);
    SeatResponseDTO getById(Long id);
    SeatResponseDTO create(SeatRequestDTO dto);
    SeatResponseDTO update(Long id, UpdateSeatRequestDTO dto);
    void delete(Long id);
}
