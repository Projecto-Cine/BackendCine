package com.cine.demo.mapper;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.model.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public Theater toEntity(TheaterRequestDTO dto) {
        return Theater.builder()
                .nombre(dto.getNombre())
                .capacidad(dto.getCapacidad())
                .build();
    }

    public TheaterResponseDTO toResponseDto(Theater theater) {
        return TheaterResponseDTO.builder()
                .id(theater.getId())
                .nombre(theater.getNombre())
                .capacidad(theater.getCapacidad())
                .totalSeats(theater.getCapacidad())
                .createdAt(theater.getCreatedAt())
                .updatedAt(theater.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateTheaterRequestDTO dto, Theater theater) {
        if (dto.getNombre() != null) theater.setNombre(dto.getNombre());
        if (dto.getCapacidad() != null) theater.setCapacidad(dto.getCapacidad());
    }
}
