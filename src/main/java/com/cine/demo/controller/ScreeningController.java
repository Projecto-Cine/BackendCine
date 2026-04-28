package com.cine.demo.controller;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    @GetMapping
    public ResponseEntity<List<ScreeningResponseDTO>> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResponseDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<ScreeningResponseDTO> create(@RequestBody ScreeningRequestDTO dto) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<ScreeningResponseDTO> update(@PathVariable Long id, @RequestBody ScreeningRequestDTO dto) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
