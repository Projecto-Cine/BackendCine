package com.cine.demo.controller;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> create(@Valid @RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PurchaseResponseDTO>builder()
                        .success(true)
                        .message("Compra creada correctamente")
                        .data(purchaseService.create(dto))
                        .build());
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PurchaseResponseDTO>builder()
                .success(true)
                .message("Compra confirmada correctamente")
                .data(purchaseService.confirm(id))
                .build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PurchaseResponseDTO>builder()
                .success(true)
                .message("Compra cancelada correctamente")
                .data(purchaseService.cancel(id))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PurchaseResponseDTO>builder()
                .success(true)
                .message("Compra obtenida correctamente")
                .data(purchaseService.getById(id))
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true)
                .message("Historial de compras obtenido correctamente")
                .data(purchaseService.getByUser(userId))
                .build());
    }

    @GetMapping("/screening/{screeningId}")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getByScreening(@PathVariable Long screeningId) {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true)
                .message("Compras de la proyección obtenidas correctamente")
                .data(purchaseService.getByScreening(screeningId))
                .build());
    }
}
