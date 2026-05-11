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
@Tag(name = "Incidencias", description = "Registro y seguimiento de incidencias del cine")
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    @Operation(summary = "Listar todas las incidencias")
    public ResponseEntity<ApiResponse<List<IncidentResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<IncidentResponseDTO>>builder()
                .success(true).message("Incidencias obtenidas correctamente").data(incidentService.findAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener incidencia por ID")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<IncidentResponseDTO>builder()
                .success(true).message("Incidencia obtenida correctamente").data(incidentService.findById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva incidencia")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> create(@Valid @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<IncidentResponseDTO>builder()
                        .success(true).message("Incidencia creada correctamente").data(incidentService.save(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar incidencia")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<IncidentResponseDTO>builder()
                .success(true).message("Incidencia actualizada correctamente").data(incidentService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar incidencia")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Incidencia eliminada correctamente").build());
    }
}
