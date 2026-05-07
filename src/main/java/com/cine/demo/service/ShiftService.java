package com.cine.demo.service;

import com.cine.demo.dto.request.ShiftRequestDTO;
import com.cine.demo.dto.request.UpdateShiftRequestDTO;
import com.cine.demo.dto.response.ShiftResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface ShiftService {
    List<ShiftResponseDTO> findAll();
    ShiftResponseDTO findById(Long id);
    List<ShiftResponseDTO> findByDate(LocalDate date);
    List<ShiftResponseDTO> findByDateRange(LocalDate from, LocalDate to);
    ShiftResponseDTO save(ShiftRequestDTO dto);
    ShiftResponseDTO update(Long id, UpdateShiftRequestDTO dto);
    void delete(Long id);
}
