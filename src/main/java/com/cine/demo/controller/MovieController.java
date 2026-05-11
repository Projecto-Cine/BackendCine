package com.cine.demo.controller;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Películas", description = "Catálogo de películas en cartelera")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "Listar todas las películas")
    public ResponseEntity<ApiResponse<List<MovieResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<MovieResponseDTO>>builder()
                .success(true).message("Películas obtenidas correctamente").data(movieService.findAll()).build());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar películas activas")
    public ResponseEntity<ApiResponse<List<MovieResponseDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.<List<MovieResponseDTO>>builder()
                .success(true).message("Películas activas obtenidas correctamente").data(movieService.findActive()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener película por ID")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<MovieResponseDTO>builder()
                .success(true).message("Película obtenida correctamente").data(movieService.findById(id)).build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear nueva película con imagen")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> createWithImage(
            @RequestPart("movie") @Valid MovieRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MovieResponseDTO>builder()
                        .success(true).message("Película creada correctamente").data(movieService.save(dto, image)).build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear nueva película")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> create(@Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MovieResponseDTO>builder()
                        .success(true).message("Película creada correctamente").data(movieService.save(dto, null)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar película")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<MovieResponseDTO>builder()
                .success(true).message("Película actualizada correctamente").data(movieService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar película")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Película eliminada correctamente").build());
    }
}
