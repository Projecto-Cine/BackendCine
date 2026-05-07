package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.MerchandiseSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchandisesales")
@RequiredArgsConstructor
public class MerchandiseSaleController {

    private final MerchandiseSaleService merchandiseSaleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MerchandiseSaleResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<MerchandiseSaleResponseDTO>>builder()
                .success(true).message("Ventas obtenidas correctamente").data(merchandiseSaleService.findAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<MerchandiseSaleResponseDTO>builder()
                .success(true).message("Venta obtenida correctamente").data(merchandiseSaleService.findById(id)).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> create(@Valid @RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MerchandiseSaleResponseDTO>builder()
                        .success(true).message("Venta registrada correctamente").data(merchandiseSaleService.save(dto)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<MerchandiseSaleResponseDTO>builder()
                .success(true).message("Venta actualizada correctamente").data(merchandiseSaleService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        merchandiseSaleService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Venta eliminada correctamente").build());
    }
}
