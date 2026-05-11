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
        UserType type = resolveUserType(dto.userType(), Boolean.TRUE.equals(dto.student()));
        return User.builder()
                .name(dto.name())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(dto.password())
                .birthDate(dto.birthDate())
                .userType(type)
                .yearlyVisits(dto.yearlyVisits() != null ? dto.yearlyVisits() : 0)
                .role(dto.role() != null ? Role.valueOf(dto.role()) : Role.CLIENT)
                .build();
    }

    public UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
                .student(user.getUserType() == UserType.STUDENT)
                .yearlyVisits(user.getYearlyVisits())
                .discountActive(user.isDiscountActive())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .imageUrl(user.getImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateUserRequestDTO dto, User user) {
        if (dto.name() != null) user.setName(dto.name());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPassword(dto.password());
        if (dto.birthDate() != null) user.setBirthDate(dto.birthDate());
        if (dto.userType() != null) user.setUserType(UserType.valueOf(dto.userType()));
        if (dto.yearlyVisits() != null) user.setYearlyVisits(dto.yearlyVisits());
        if (dto.role() != null) user.setRole(Role.valueOf(dto.role()));
    }

    private UserType resolveUserType(String userTypeStr, boolean student) {
        if (userTypeStr != null) return UserType.valueOf(userTypeStr);
        return student ? UserType.STUDENT : UserType.ADULT;
    }
}
