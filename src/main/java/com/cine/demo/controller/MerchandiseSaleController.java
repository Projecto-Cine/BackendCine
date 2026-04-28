package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.MerchandiseSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchandisesales")
@RequiredArgsConstructor
public class MerchandiseSaleController {

    private final MerchandiseSaleService merchandiseSaleService;

    @GetMapping
    public ResponseEntity<List<MerchandiseSaleResponseDTO>> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<MerchandiseSaleResponseDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<MerchandiseSaleResponseDTO> create(@RequestBody MerchandiseSaleRequestDTO dto) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<MerchandiseSaleResponseDTO> update(@PathVariable Long id, @RequestBody MerchandiseSaleRequestDTO dto) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
