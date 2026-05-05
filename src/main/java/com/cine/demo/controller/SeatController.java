package com.cine.demo.controller;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<List<SeatResponseDTO>> getAll() {
        return ResponseEntity.ok(seatService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SeatResponseDTO> create(@Valid @RequestBody SeatRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSeatRequestDTO dto) {
        return ResponseEntity.ok(seatService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}