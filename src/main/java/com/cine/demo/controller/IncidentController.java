package com.cine.demo.controller;

import com.cine.demo.dto.request.IncidentRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.IncidentResponseDTO;
import com.cine.demo.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "Cinema incident logging and tracking")
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    @Operation(summary = "List all incidents")
    public ResponseEntity<ApiResponse<List<IncidentResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<IncidentResponseDTO>>builder()
                .success(true).message("Incidents retrieved successfully").data(incidentService.findAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<IncidentResponseDTO>builder()
                .success(true).message("Incident retrieved successfully").data(incidentService.findById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Create new incident")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> create(@Valid @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<IncidentResponseDTO>builder()
                        .success(true).message("Incident created successfully").data(incidentService.save(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update incident")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<IncidentResponseDTO>builder()
                .success(true).message("Incident updated successfully").data(incidentService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete incident")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Incident deleted successfully").build());
    }
}
