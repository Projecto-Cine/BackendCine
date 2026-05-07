package com.cine.demo.controller;

import com.cine.demo.dto.request.SocioRequestDTO;
import com.cine.demo.dto.request.UpdateSocioRequestDTO;
import com.cine.demo.dto.response.SocioResponseDTO;
import com.cine.demo.service.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    @GetMapping
    public ResponseEntity<List<SocioResponseDTO>> getAll() {
        return ResponseEntity.ok(socioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocioResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(socioService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SocioResponseDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(socioService.search(q));
    }

    @PostMapping
    public ResponseEntity<SocioResponseDTO> create(@Valid @RequestBody SocioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socioService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocioResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSocioRequestDTO dto) {
        return ResponseEntity.ok(socioService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        socioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}