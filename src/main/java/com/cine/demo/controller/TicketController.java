package com.cine.demo.controller;

import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> create(@RequestBody TicketRequestDTO dto) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> update(@PathVariable Long id, @RequestBody TicketRequestDTO dto) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
