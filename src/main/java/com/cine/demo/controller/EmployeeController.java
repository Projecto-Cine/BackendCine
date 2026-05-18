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
        return ResponseEntity.ok(ApiResponse.ok("Employees retrieved successfully", employeeService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Employee retrieved successfully", employeeService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create new employee")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Employee created successfully", employeeService.save(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated successfully", employeeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Not allowed if the employee has assigned shifts")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully"));
    }
}
