package com.cine.demo.service.impl;

import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.mapper.PurchaseMapper;
import com.cine.demo.repository.TicketRepository;
import com.cine.demo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    public List<TicketResponseDTO> getByPurchase(Long purchaseId) {
        return ticketRepository.findByPurchaseId(purchaseId).stream()
                .map(purchaseMapper::toTicketResponseDto)
                .toList();
    }

    @Override
    public List<TicketResponseDTO> getByScreening(Long screeningId) {
        return ticketRepository.findByScreeningId(screeningId).stream()
                .map(purchaseMapper::toTicketResponseDto)
                .toList();
    }
}
