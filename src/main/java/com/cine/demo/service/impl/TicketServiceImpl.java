package com.cine.demo.service.impl;

import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    @Override
    public List<TicketResponseDTO> findAll() { return null; }

    @Override
    public TicketResponseDTO findById(Long id) { return null; }

    @Override
    public TicketResponseDTO save(TicketRequestDTO dto) { return null; }

    @Override
    public TicketResponseDTO update(Long id, TicketRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
