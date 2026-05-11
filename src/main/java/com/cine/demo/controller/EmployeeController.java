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
@Tag(name = "Employees", description = "Cinema employee management (cashiers, security, cleaning, management)")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "List all employees")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponseDTO>>builder()
                .success(true).message("Employees retrieved successfully").data(employeeService.findAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<EmployeeResponseDTO>builder()
                .success(true).message("Employee retrieved successfully").data(employeeService.findById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Create new employee")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EmployeeResponseDTO>builder()
                        .success(true).message("Employee created successfully").data(employeeService.save(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<EmployeeResponseDTO>builder()
                .success(true).message("Employee updated successfully").data(employeeService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Not allowed if the employee has assigned shifts")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Employee deleted successfully").build());
    }
}
