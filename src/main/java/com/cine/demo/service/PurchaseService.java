package com.cine.demo.service;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import java.util.List;

public interface PurchaseService {
    PurchaseResponseDTO create(PurchaseRequestDTO dto);
    PurchaseResponseDTO confirm(Long purchaseId);
    PurchaseResponseDTO cancel(Long purchaseId);
    PurchaseResponseDTO getById(Long id);
    List<PurchaseResponseDTO> getAll();
    List<PurchaseResponseDTO> getByUser(Long userId);
    List<PurchaseResponseDTO> getByScreening(Long screeningId);
}
