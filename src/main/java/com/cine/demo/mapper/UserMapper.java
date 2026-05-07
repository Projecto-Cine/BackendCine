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
                .name(dto.getName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .dateOfBirth(dto.getDateOfBirth())
                .student(dto.isStudent())
                .visitsPerYear(dto.getVisitsPerYear())
                .role(dto.getRole() != null ? Role.valueOf(dto.getRole()) : Role.CLIENT)
                .status(dto.getStatus() != null ? dto.getStatus() : "active")
                .build();
    }

    public UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .student(user.isStudent())
                .visitsPerYear(user.getVisitsPerYear())
                .role(user.getRole().name())
                .status(user.getStatus())
                .imageUrl(user.getImageUrl())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateUserRequestDTO dto, User user) {
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getStudent() != null) user.setStudent(dto.getStudent());
        if (dto.getVisitsPerYear() != null) user.setVisitsPerYear(dto.getVisitsPerYear());
        if (dto.getRole() != null) user.setRole(Role.valueOf(dto.getRole()));
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
    }
}