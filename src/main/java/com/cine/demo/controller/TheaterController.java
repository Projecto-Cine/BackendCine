package com.cine.demo.controller;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping
    public ResponseEntity<List<TheaterResponseDTO>> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterResponseDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<TheaterResponseDTO> create(@RequestBody TheaterRequestDTO dto) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterResponseDTO> update(@PathVariable Long id, @RequestBody TheaterRequestDTO dto) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
