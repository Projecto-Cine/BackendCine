package com.cine.demo.service;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.TaquillaRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.TaquillaResponseDTO;
import java.util.List;

public interface PurchaseService {
    PurchaseResponseDTO create(PurchaseRequestDTO dto);
    TaquillaResponseDTO createFromTaquilla(TaquillaRequestDTO dto);
    PurchaseResponseDTO confirm(Long purchaseId);
    PurchaseResponseDTO cancel(Long purchaseId);
    PurchaseResponseDTO getById(Long id);
    List<PurchaseResponseDTO> getAll();
    List<PurchaseResponseDTO> getByUser(Long userId);
    List<PurchaseResponseDTO> getByScreening(Long screeningId);
}
