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
                .annualVisits(dto.annualVisits() != null ? dto.annualVisits() : 0)
                .discountActive(Boolean.TRUE.equals(dto.discountActive()))
                .role(dto.role() != null ? resolveRole(dto.role()) : Role.CLIENT)
                .build();
    }

    public UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .dateOfBirth(user.getBirthDate())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
                .student(user.getUserType() == UserType.STUDENT)
                .annualVisits(user.getAnnualVisits())
                .visitsPerYear(user.getAnnualVisits())
                .discountActive(user.isDiscountActive())
                .fidelityDiscountEligible(user.getAnnualVisits() >= 10)
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status("ACTIVE")
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
        if (dto.annualVisits() != null) user.setAnnualVisits(dto.annualVisits());
        if (dto.discountActive() != null) user.setDiscountActive(dto.discountActive());
        if (dto.role() != null) user.setRole(resolveRole(dto.role()));
    }

    private Role resolveRole(String roleStr) {
        return Role.valueOf(roleStr.toUpperCase());
    }

    private UserType resolveUserType(String userTypeStr, boolean student) {
        if (userTypeStr != null) return UserType.valueOf(userTypeStr);
        return student ? UserType.STUDENT : UserType.ADULT;
    }
}
