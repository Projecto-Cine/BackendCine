package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.request.UpdateMovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MovieMapper;
import com.cine.demo.model.Movie;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponseDTO> getAll() {
        return movieRepository.findAll().stream()
                .map(movieMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponseDTO getById(Long id) {
        return movieMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    public MovieResponseDTO create(MovieRequestDTO dto) {
        if (movieRepository.existsByTitulo(dto.getTitulo())) {
            throw new ConflictException("Ya existe una película con el título: " + dto.getTitulo());
        }
        return movieMapper.toResponseDto(movieRepository.save(movieMapper.toEntity(dto)));
    }

    @Override
    public MovieResponseDTO update(Long id, UpdateMovieRequestDTO dto) {
        Movie movie = findOrThrow(id);
        movieMapper.updateEntityFromDto(dto, movie);
        return movieMapper.toResponseDto(movieRepository.save(movie));
    }

    @Override
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Película no encontrada con id: " + id);
        }
        movieRepository.deleteById(id);
    }

    @Override
    public MovieResponseDTO uploadPoster(Long id, MultipartFile file) {
        Movie movie = findOrThrow(id);
        movie.setPosterUrl(cloudinaryService.uploadImage(file, "posters"));
        return movieMapper.toResponseDto(movieRepository.save(movie));
    }

    private Movie findOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + id));
    }
}
