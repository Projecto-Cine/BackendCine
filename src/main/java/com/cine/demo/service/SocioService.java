package com.cine.demo.service;

import com.cine.demo.dto.request.SocioRequestDTO;
import com.cine.demo.dto.request.UpdateSocioRequestDTO;
import com.cine.demo.dto.response.SocioResponseDTO;

import java.util.List;

public interface SocioService {
    List<SocioResponseDTO> getAll();
    SocioResponseDTO getById(Long id);
    List<SocioResponseDTO> search(String query);
    SocioResponseDTO create(SocioRequestDTO dto);
    SocioResponseDTO update(Long id, UpdateSocioRequestDTO dto);
    void delete(Long id);
}