package com.cine.demo.service;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import java.util.List;

public interface SeatService {
    List<SeatResponseDTO> findAll();
    SeatResponseDTO findById(Long id);
    SeatResponseDTO save(SeatRequestDTO dto);
    SeatResponseDTO update(Long id, SeatRequestDTO dto);
    void delete(Long id);
}
