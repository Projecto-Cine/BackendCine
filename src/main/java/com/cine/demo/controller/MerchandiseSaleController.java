package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.MerchandiseSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchandisesales")
@RequiredArgsConstructor
@Tag(name = "Merchandise Sales", description = "Record of cinema merchandise sales")
public class MerchandiseSaleController {

    private final MerchandiseSaleService merchandiseSaleService;

    @GetMapping
    @Operation(summary = "List all sales")
    public ResponseEntity<ApiResponse<List<MerchandiseSaleResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<MerchandiseSaleResponseDTO>>builder()
                .success(true).message("Sales retrieved successfully").data(merchandiseSaleService.findAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<MerchandiseSaleResponseDTO>builder()
                .success(true).message("Sale retrieved successfully").data(merchandiseSaleService.findById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Register new sale")
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> create(@Valid @RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MerchandiseSaleResponseDTO>builder()
                        .success(true).message("Sale registered successfully").data(merchandiseSaleService.save(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sale")
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<MerchandiseSaleResponseDTO>builder()
                .success(true).message("Sale updated successfully").data(merchandiseSaleService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sale")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        merchandiseSaleService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Sale deleted successfully").build());
    }
}
