package com.cine.demo.service;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import java.util.List;

public interface TheaterService {
    List<TheaterResponseDTO> getAll();
    TheaterResponseDTO getById(Long id);
    TheaterResponseDTO create(TheaterRequestDTO dto);
    TheaterResponseDTO update(Long id, UpdateTheaterRequestDTO dto);
    void delete(Long id);
}
