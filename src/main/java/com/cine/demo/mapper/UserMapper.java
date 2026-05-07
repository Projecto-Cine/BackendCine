package com.cine.demo.mapper;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        UserType type = resolveUserType(dto.getUserType(), Boolean.TRUE.equals(dto.getEsEstudiante()));
        return User.builder()
                .nombre(dto.getNombre())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fechaNacimiento(dto.getFechaNacimiento())
                .userType(type)
                .visitasAnio(dto.getVisitasAnio() != null ? dto.getVisitasAnio() : 0)
                .rol(dto.getRol() != null ? Role.valueOf(dto.getRol()) : Role.CLIENTE)
                .build();
    }

    public UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .fechaNacimiento(user.getFechaNacimiento())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
                .esEstudiante(user.getUserType() == UserType.STUDENT)
                .visitasAnio(user.getVisitasAnio())
                .discountActive(user.isDiscountActive())
                .rol(user.getRol() != null ? user.getRol().name() : null)
                .imagenUrl(user.getImagenUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateUserRequestDTO dto, User user) {
        if (dto.getNombre() != null) user.setNombre(dto.getNombre());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getFechaNacimiento() != null) user.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getUserType() != null) user.setUserType(UserType.valueOf(dto.getUserType()));
        if (dto.getVisitasAnio() != null) user.setVisitasAnio(dto.getVisitasAnio());
        if (dto.getRol() != null) user.setRol(Role.valueOf(dto.getRol()));
    }

    private UserType resolveUserType(String userTypeStr, boolean esEstudiante) {
        if (userTypeStr != null) return UserType.valueOf(userTypeStr);
        return esEstudiante ? UserType.STUDENT : UserType.ADULT;
    }
}
