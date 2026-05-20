package com.cine.demo.service;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import java.util.List;

public interface PurchaseService {
    PurchaseResponseDTO create(PurchaseRequestDTO dto);
    PurchaseResponseDTO confirm(Long purchaseId, PaymentMethod paymentMethod);
    PurchaseResponseDTO cancel(Long purchaseId);
    PurchaseResponseDTO getById(Long id);
    List<PurchaseResponseDTO> getAll(PurchaseStatus status);
    List<PurchaseResponseDTO> getByUser(Long userId);
    List<PurchaseResponseDTO> getByScreening(Long screeningId);
}
