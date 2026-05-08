package com.cine.demo.service;

import com.cine.demo.dto.response.TicketResponseDTO;
import java.util.List;

public interface TicketService {
    List<TicketResponseDTO> findAll();
    TicketResponseDTO findById(Long id);
    List<TicketResponseDTO> getByPurchase(Long purchaseId);
    List<TicketResponseDTO> getByScreening(Long screeningId);
}
