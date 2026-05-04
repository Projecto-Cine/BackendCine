package com.cine.demo.service;

import com.cine.demo.dto.request.IncidentRequestDTO;
import com.cine.demo.dto.response.IncidentResponseDTO;
import java.util.List;

public interface IncidentService {
    List<IncidentResponseDTO> getAll();
    IncidentResponseDTO getById(Long id);
    IncidentResponseDTO create(IncidentRequestDTO dto);
    IncidentResponseDTO update(Long id, IncidentRequestDTO dto);
    void delete(Long id);
}