package com.cine.demo.service;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import java.util.List;

public interface PurchaseService {
    List<PurchaseResponseDTO> findAll();
    PurchaseResponseDTO findById(Long id);
    PurchaseResponseDTO save(PurchaseRequestDTO dto);
    PurchaseResponseDTO update(Long id, PurchaseRequestDTO dto);
    void delete(Long id);
}
