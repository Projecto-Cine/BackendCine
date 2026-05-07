package com.cine.demo.controller;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.request.UpdateMovieRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .success(true).message("Películas obtenidas correctamente").data(movieService.getAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener película por ID")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<MovieResponseDTO>builder()
                .success(true).message("Película obtenida correctamente").data(movieService.getById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva película")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> create(@Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MovieResponseDTO>builder()
                        .success(true).message("Película creada correctamente").data(movieService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar película")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateMovieRequestDTO dto) {
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

    @PostMapping("/{id}/poster")
    @Operation(summary = "Subir póster de la película")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> uploadPoster(
            @PathVariable Long id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.<MovieResponseDTO>builder()
                .success(true).message("Póster subido correctamente").data(movieService.uploadPoster(id, file)).build());
    }
}
