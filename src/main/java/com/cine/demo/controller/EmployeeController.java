package com.cine.demo.controller;

import com.cine.demo.dto.request.EmployeeRequestDTO;
import com.cine.demo.dto.request.UpdateEmployeeRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import com.cine.demo.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Empleados", description = "Gestión de empleados del cine (cajeros, seguridad, limpieza, gerencia)")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Listar todos los empleados")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponseDTO>>builder()
                .success(true).message("Trabajadores obtenidos correctamente").data(employeeService.findAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empleado por ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<EmployeeResponseDTO>builder()
                .success(true).message("Trabajador obtenido correctamente").data(employeeService.findById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo empleado")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EmployeeResponseDTO>builder()
                        .success(true).message("Trabajador creado correctamente").data(employeeService.save(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar empleado")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<EmployeeResponseDTO>builder()
                .success(true).message("Trabajador actualizado correctamente").data(employeeService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar empleado", description = "No permitido si el empleado tiene turnos asignados")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Trabajador eliminado correctamente").build());
    }
}
