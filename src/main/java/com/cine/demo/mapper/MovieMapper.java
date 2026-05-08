package com.cine.demo.mapper;

import com.cine.demo.dto.request.MovieRequestDTO;
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
                .language(dto.getLanguage())
                .schedule(dto.getSchedule())
                .build();
    }

    public MovieResponseDTO toResponseDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .titulo(movie.getTitulo())
                .descripcion(movie.getDescripcion())
                .duracionMin(movie.getDuracionMin())
                .genero(movie.getGenero())
                .clasificacionEdad(movie.getClasificacionEdad() != null ? movie.getClasificacionEdad().name() : null)
                .posterUrl(movie.getPosterUrl())
                .active(movie.isActive())
                .language(movie.getLanguage())
                .schedule(movie.getSchedule())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateMovieRequestDTO dto, Movie movie) {
        if (dto.getTitulo() != null) movie.setTitulo(dto.getTitulo());
        if (dto.getDescripcion() != null) movie.setDescripcion(dto.getDescripcion());
        if (dto.getDuracionMin() != null) movie.setDuracionMin(dto.getDuracionMin());
        if (dto.getGenero() != null) movie.setGenero(dto.getGenero());
        if (dto.getClasificacionEdad() != null) movie.setClasificacionEdad(dto.getClasificacionEdad());
        if (dto.getLanguage() != null) movie.setLanguage(dto.getLanguage());
        if (dto.getSchedule() != null) movie.setSchedule(dto.getSchedule());
    }
}
