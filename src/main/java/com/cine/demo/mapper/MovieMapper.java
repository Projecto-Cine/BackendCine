package com.cine.demo.mapper;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.request.UpdateMovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO dto) {
        return Movie.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .duracionMin(dto.getDuracionMin())
                .genero(dto.getGenero())
                .clasificacionEdad(dto.getClasificacionEdad())
                .build();
    }

    public MovieResponseDTO toResponseDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .titulo(movie.getTitulo())
                .descripcion(movie.getDescripcion())
                .duracionMin(movie.getDuracionMin())
                .genero(movie.getGenero())
                .clasificacionEdad(movie.getClasificacionEdad())
                .posterUrl(movie.getPosterUrl())
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateMovieRequestDTO dto, Movie movie) {
        if (dto.getTitulo() != null) movie.setTitulo(dto.getTitulo());
        if (dto.getDescripcion() != null) movie.setDescripcion(dto.getDescripcion());
        if (dto.getDuracionMin() != null) movie.setDuracionMin(dto.getDuracionMin());
        if (dto.getGenero() != null) movie.setGenero(dto.getGenero());
        if (dto.getClasificacionEdad() != null) movie.setClasificacionEdad(dto.getClasificacionEdad());
    }
}
