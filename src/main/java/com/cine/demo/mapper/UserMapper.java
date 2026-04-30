package com.cine.demo.mapper;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fechaNacimiento(dto.getFechaNacimiento())
                .esEstudiante(dto.isEsEstudiante())
                .visitasAnio(dto.getVisitasAnio())
                .rol(dto.getRol() != null ? Role.valueOf(dto.getRol()) : Role.CLIENTE)
                .build();
    }

    public UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .email(user.getEmail())
                .fechaNacimiento(user.getFechaNacimiento())
                .esEstudiante(user.isEsEstudiante())
                .visitasAnio(user.getVisitasAnio())
                .rol(user.getRol().name())
                .imagenUrl(user.getImagenUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateUserRequestDTO dto, User user) {
        if (dto.getNombre() != null) user.setNombre(dto.getNombre());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFechaNacimiento() != null) user.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getEsEstudiante() != null) user.setEsEstudiante(dto.getEsEstudiante());
        if (dto.getVisitasAnio() != null) user.setVisitasAnio(dto.getVisitasAnio());
        if (dto.getRol() != null) user.setRol(Role.valueOf(dto.getRol()));
    }
}
