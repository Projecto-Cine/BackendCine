package com.cine.demo.service;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import java.util.List;

public interface TheaterService {
    List<TheaterResponseDTO> findAll();
    TheaterResponseDTO findById(Long id);
    TheaterResponseDTO save(TheaterRequestDTO dto);
    TheaterResponseDTO update(Long id, TheaterRequestDTO dto);
    void delete(Long id);
}
