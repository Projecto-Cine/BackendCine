package com.cine.demo.service;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import java.util.List;

public interface MerchandiseSaleService {
    List<MerchandiseSaleResponseDTO> findAll();
    MerchandiseSaleResponseDTO findById(Long id);
    MerchandiseSaleResponseDTO save(MerchandiseSaleRequestDTO dto);
    MerchandiseSaleResponseDTO update(Long id, MerchandiseSaleRequestDTO dto);
    void delete(Long id);
}
