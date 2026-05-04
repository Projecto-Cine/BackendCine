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
                .title(dto.getTitle())
                .description(dto.getDescription())
                .director(dto.getDirector())
                .year(dto.getYear())
                .durationMin(dto.getDurationMin())
                .genre(dto.getGenre())
                .language(dto.getLanguage())
                .format(dto.getFormat())
                .ageRating(dto.getAgeRating())
                .imageUrl(dto.getImageUrl())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public MovieResponseDTO toResponseDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .director(movie.getDirector())
                .year(movie.getYear())
                .durationMin(movie.getDurationMin())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .format(movie.getFormat())
                .ageRating(movie.getAgeRating())
                .imageUrl(movie.getImageUrl())
                .active(movie.getActive())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateMovieRequestDTO dto, Movie movie) {
        if (dto.getTitle() != null) movie.setTitle(dto.getTitle());
        if (dto.getDescription() != null) movie.setDescription(dto.getDescription());
        if (dto.getDirector() != null) movie.setDirector(dto.getDirector());
        if (dto.getYear() != null) movie.setYear(dto.getYear());
        if (dto.getDurationMin() != null) movie.setDurationMin(dto.getDurationMin());
        if (dto.getGenre() != null) movie.setGenre(dto.getGenre());
        if (dto.getLanguage() != null) movie.setLanguage(dto.getLanguage());
        if (dto.getFormat() != null) movie.setFormat(dto.getFormat());
        if (dto.getAgeRating() != null) movie.setAgeRating(dto.getAgeRating());
        if (dto.getImageUrl() != null) movie.setImageUrl(dto.getImageUrl());
        if (dto.getActive() != null) movie.setActive(dto.getActive());
    }
}