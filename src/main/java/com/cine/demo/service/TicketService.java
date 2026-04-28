package com.cine.demo.service;

import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.TicketResponseDTO;
import java.util.List;

public interface TicketService {
    List<TicketResponseDTO> findAll();
    TicketResponseDTO findById(Long id);
    TicketResponseDTO save(TicketRequestDTO dto);
    TicketResponseDTO update(Long id, TicketRequestDTO dto);
    void delete(Long id);
}
