package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchandises")
@RequiredArgsConstructor
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    @GetMapping
    public ResponseEntity<List<MerchandiseResponseDTO>> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<MerchandiseResponseDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<MerchandiseResponseDTO> create(@RequestBody MerchandiseRequestDTO dto) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<MerchandiseResponseDTO> update(@PathVariable Long id, @RequestBody MerchandiseRequestDTO dto) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
