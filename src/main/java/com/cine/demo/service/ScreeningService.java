package com.cine.demo.service;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import java.util.List;

public interface ScreeningService {
    List<ScreeningResponseDTO> findAll();
    ScreeningResponseDTO findById(Long id);
    ScreeningResponseDTO save(ScreeningRequestDTO dto);
    ScreeningResponseDTO update(Long id, ScreeningRequestDTO dto);
    void delete(Long id);
}
