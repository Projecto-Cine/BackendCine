package com.cine.demo.controller;

import com.cine.demo.dto.request.UpdateWorkerRequestDTO;
import com.cine.demo.dto.request.WorkerRequestDTO;
import com.cine.demo.dto.response.WorkerResponseDTO;
import com.cine.demo.security.AuthContext;
import com.cine.demo.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping
    public ResponseEntity<List<WorkerResponseDTO>> getAll() {
        return ResponseEntity.ok(workerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<WorkerResponseDTO> create(@Valid @RequestBody WorkerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkerRequestDTO dto) {
        return ResponseEntity.ok(workerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long requesterId = AuthContext.isAuthenticated() ? AuthContext.get().getId() : null;
        workerService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }
}