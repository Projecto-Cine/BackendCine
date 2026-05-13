package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.service.MerchandiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchandise")
@RequiredArgsConstructor
@Tag(name = "Merchandise", description = "Sales item catalog (food, drinks, merchandise)")
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    @GetMapping
    @Operation(summary = "List all items")
    public ResponseEntity<ApiResponse<List<MerchandiseResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Items retrieved successfully", merchandiseService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<ApiResponse<MerchandiseResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Item retrieved successfully", merchandiseService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create new item")
    public ResponseEntity<ApiResponse<MerchandiseResponseDTO>> create(@Valid @RequestBody MerchandiseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Item created successfully", merchandiseService.save(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item")
    public ResponseEntity<ApiResponse<MerchandiseResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody MerchandiseRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Item updated successfully", merchandiseService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        merchandiseService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Item deleted successfully"));
    }
}
