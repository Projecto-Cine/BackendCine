package com.cine.demo.controller;

import com.cine.demo.dto.response.ClientSummaryDTO;
import com.cine.demo.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientSummaryDTO>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientSummaryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientSummaryDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(clientService.search(q));
    }
}