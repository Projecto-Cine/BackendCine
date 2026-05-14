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
@Tag(name = "Movies", description = "Movie catalog on billboard")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "List all movies")
    public ResponseEntity<ApiResponse<List<MovieResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Movies retrieved successfully", movieService.findAll()));
    }

    @GetMapping("/active")
    @Operation(summary = "List active movies")
    public ResponseEntity<ApiResponse<List<MovieResponseDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok("Active movies retrieved successfully", movieService.findActive()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Movie retrieved successfully", movieService.findById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new movie with image")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> createWithImage(
            @RequestPart("movie") @Valid MovieRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movie created successfully", movieService.save(dto, image)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new movie")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> create(@Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movie created successfully", movieService.save(dto, null)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update movie")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Movie updated successfully", movieService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete movie")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Movie deleted successfully"));
    }
}
