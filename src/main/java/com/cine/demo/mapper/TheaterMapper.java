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
                .name(dto.name())
                .capacity(dto.capacity())
                .build();
    }

    public TheaterResponseDTO toResponseDto(Theater theater) {
        return TheaterResponseDTO.builder()
                .id(theater.getId())
                .name(theater.getName())
                .capacity(theater.getCapacity())
                .numRows(theater.getNumRows())
                .numColumns(theater.getNumColumns())
                .totalSeats(theater.getSeats().size())
                .build();
    }

    public void updateEntityFromDto(UpdateTheaterRequestDTO dto, Theater theater) {
        if (dto.name() != null) theater.setName(dto.name());
        if (dto.capacity() != null) theater.setCapacity(dto.capacity());
    }
}
