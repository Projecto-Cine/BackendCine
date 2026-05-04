package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.MerchandiseSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchandise/sales")
@RequiredArgsConstructor
public class MerchandiseSaleController {

    private final MerchandiseSaleService merchandiseSaleService;

    @GetMapping
    public ResponseEntity<List<MerchandiseSaleResponseDTO>> getAll() {
        return ResponseEntity.ok(merchandiseSaleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchandiseSaleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(merchandiseSaleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MerchandiseSaleResponseDTO> create(@RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(merchandiseSaleService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MerchandiseSaleResponseDTO> update(
            @PathVariable Long id, @RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.ok(merchandiseSaleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        merchandiseSaleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}