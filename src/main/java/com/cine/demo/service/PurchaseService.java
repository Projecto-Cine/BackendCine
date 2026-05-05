package com.cine.demo.service;

import com.cine.demo.dto.request.PaymentRequestDTO;
import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.ReservationRequestDTO;
import com.cine.demo.dto.request.TicketOfficeRequestDTO;
import com.cine.demo.dto.response.PaymentResponseDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.TicketOfficeResponseDTO;
import java.util.List;

public interface PurchaseService {
    PurchaseResponseDTO create(PurchaseRequestDTO dto);
    PurchaseResponseDTO createReservation(ReservationRequestDTO dto);
    PurchaseResponseDTO updateReservation(Long id, ReservationRequestDTO dto);
    TicketOfficeResponseDTO createFromTicketOffice(TicketOfficeRequestDTO dto);
    PaymentResponseDTO pay(Long purchaseId, PaymentRequestDTO dto);
    PurchaseResponseDTO confirm(Long purchaseId);
    PurchaseResponseDTO cancel(Long purchaseId);
    PurchaseResponseDTO getById(Long id);
    List<PurchaseResponseDTO> getAll();
    List<PurchaseResponseDTO> getByUser(Long userId);
    List<PurchaseResponseDTO> getByScreening(Long screeningId);
    void sendConfirmationEmail(Long purchaseId);
}