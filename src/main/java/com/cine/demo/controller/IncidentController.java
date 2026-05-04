package com.cine.demo.controller;

import com.cine.demo.dto.request.IncidentRequestDTO;
import com.cine.demo.dto.response.IncidentResponseDTO;
import com.cine.demo.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseEntity<List<IncidentResponseDTO>> getAll() {
        return ResponseEntity.ok(incidentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<IncidentResponseDTO> create(@Valid @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> update(
            @PathVariable Long id, @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.ok(incidentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}