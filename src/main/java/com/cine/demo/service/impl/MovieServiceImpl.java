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
                .title(dto.getTitle())
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .durationMin(dto.getDurationMin())
                .ageRating(dto.getAgeRating())
                .posterUrl(saveImage(image))
                .build();
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    @Override
    public MovieResponseDTO update(Long id, MovieRequestDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDurationMin(dto.getDurationMin());
        movie.setAgeRating(dto.getAgeRating());
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
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .durationMin(movie.getDurationMin())
                .ageRating(movie.getAgeRating() != null ? movie.getAgeRating().name() : null)
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
