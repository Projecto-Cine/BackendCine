package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.service.MerchandiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchandise")
@RequiredArgsConstructor
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MerchandiseResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<MerchandiseResponseDTO>>builder()
                .success(true).message("Artículos obtenidos correctamente").data(merchandiseService.findAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchandiseResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<MerchandiseResponseDTO>builder()
                .success(true).message("Artículo obtenido correctamente").data(merchandiseService.findById(id)).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MerchandiseResponseDTO>> create(@Valid @RequestBody MerchandiseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MerchandiseResponseDTO>builder()
                        .success(true).message("Artículo creado correctamente").data(merchandiseService.save(dto)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchandiseResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody MerchandiseRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<MerchandiseResponseDTO>builder()
                .success(true).message("Artículo actualizado correctamente").data(merchandiseService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        merchandiseService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Artículo eliminado correctamente").build());
    }
}
