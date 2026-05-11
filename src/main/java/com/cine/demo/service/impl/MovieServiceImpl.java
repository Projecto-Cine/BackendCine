package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<MovieResponseDTO> findAll() {
        return movieRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponseDTO> findActive() {
        return movieRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponseDTO findById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return toDTO(movie);
    }

    @Override
    public MovieResponseDTO save(MovieRequestDTO dto, MultipartFile image) {
        Movie movie = Movie.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .genero(dto.getGenero())
                .duracionMin(dto.getDuracionMin())
                .clasificacionEdad(dto.getClasificacionEdad())
                .posterUrl(saveImage(image))
                .build();
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    @Override
    public MovieResponseDTO update(Long id, MovieRequestDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        movie.setTitulo(dto.getTitulo());
        movie.setDescripcion(dto.getDescripcion());
        movie.setGenero(dto.getGenero());
        movie.setDuracionMin(dto.getDuracionMin());
        movie.setClasificacionEdad(dto.getClasificacionEdad());
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    @Override
    public void delete(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        movie.setActive(false);
        movieRepository.save(movie);
    }

    private MovieResponseDTO toDTO(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .titulo(movie.getTitulo())
                .descripcion(movie.getDescripcion())
                .genero(movie.getGenero())
                .duracionMin(movie.getDuracionMin())
                .clasificacionEdad(movie.getClasificacionEdad() != null ? movie.getClasificacionEdad().name() : null)
                .posterUrl(movie.getPosterUrl())
                .active(movie.isActive())
                .language(movie.getLanguage())
                .schedule(movie.getSchedule())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        try {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path path = Paths.get("uploads/movies/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
            return "/uploads/movies/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image", e);
        }
    }
}
