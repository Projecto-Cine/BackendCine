package com.cine.demo.controller;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<List<SeatResponseDTO>> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponseDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<SeatResponseDTO> create(@RequestBody SeatRequestDTO dto) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<SeatResponseDTO> update(@PathVariable Long id, @RequestBody SeatRequestDTO dto) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
