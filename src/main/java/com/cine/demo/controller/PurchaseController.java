package com.cine.demo.controller;

import com.cine.demo.dto.request.ConfirmPurchaseRequestDTO;
import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.model.enums.PurchaseStatus;
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
    @Operation(summary = "List all purchases, optionally filtered by status (PENDING, PAID, CANCELLED)")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getAll(
            @RequestParam(required = false) PurchaseStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Purchases retrieved successfully", purchaseService.getAll(status)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase by ID")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase retrieved successfully", purchaseService.getById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Purchase history for a user")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase history retrieved successfully", purchaseService.getByUser(userId)));
    }

    @GetMapping("/screening/{screeningId}")
    @Operation(summary = "Purchases for a screening")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getByScreening(@PathVariable Long screeningId) {
        return ResponseEntity.ok(ApiResponse.ok("Screening purchases retrieved successfully", purchaseService.getByScreening(screeningId)));
    }

    @PostMapping
    @Operation(summary = "Create new purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> create(@Valid @RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Purchase created successfully", purchaseService.create(dto)));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm and pay a purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> confirm(
            @PathVariable Long id,
            @RequestBody(required = false) ConfirmPurchaseRequestDTO body) {
        var paymentMethod = body != null ? body.paymentMethod() : null;
        return ResponseEntity.ok(ApiResponse.ok("Purchase confirmed successfully", purchaseService.confirm(id, paymentMethod)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase cancelled successfully", purchaseService.cancel(id)));
    }
}
