package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchandise")
@RequiredArgsConstructor
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    @GetMapping
    public ResponseEntity<List<MerchandiseResponseDTO>> getAll() {
        return ResponseEntity.ok(merchandiseService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<MerchandiseResponseDTO>> getActive() {
        return ResponseEntity.ok(merchandiseService.findActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchandiseResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(merchandiseService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MerchandiseResponseDTO> create(@RequestBody MerchandiseRequestDTO dto) {
        return ResponseEntity.ok(merchandiseService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MerchandiseResponseDTO> update(@PathVariable Long id, @RequestBody MerchandiseRequestDTO dto) {
        return ResponseEntity.ok(merchandiseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        merchandiseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
