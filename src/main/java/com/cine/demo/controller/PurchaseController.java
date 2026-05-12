package com.cine.demo.controller;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Ticket purchase management")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    @Operation(summary = "List all purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true).message("Purchases retrieved successfully").data(purchaseService.getAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase by ID")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PurchaseResponseDTO>builder()
                .success(true).message("Purchase retrieved successfully").data(purchaseService.getById(id)).build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Purchase history for a user")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true).message("Purchase history retrieved successfully").data(purchaseService.getByUser(userId)).build());
    }

    @GetMapping("/screening/{screeningId}")
    @Operation(summary = "Purchases for a screening")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getByScreening(@PathVariable Long screeningId) {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true).message("Screening purchases retrieved successfully").data(purchaseService.getByScreening(screeningId)).build());
    }

    @PostMapping
    @Operation(summary = "Create new purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> create(@Valid @RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PurchaseResponseDTO>builder()
                        .success(true).message("Purchase created successfully").data(purchaseService.create(dto)).build());
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm and pay a purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PurchaseResponseDTO>builder()
                .success(true).message("Purchase confirmed successfully").data(purchaseService.confirm(id)).build());
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PurchaseResponseDTO>builder()
                .success(true).message("Purchase cancelled successfully").data(purchaseService.cancel(id)).build());
    }
}
