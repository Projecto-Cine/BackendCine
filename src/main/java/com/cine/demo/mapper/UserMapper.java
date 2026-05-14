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
        UserType type = resolveUserType(dto.getUserType(), Boolean.TRUE.equals(dto.getStudent()));
        return User.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .birthDate(dto.getBirthDate())
                .userType(type)
                .annualVisits(dto.getAnnualVisits() != null ? dto.getAnnualVisits() : 0)
                .role(dto.getRole() != null ? resolveRole(dto.getRole()) : Role.CLIENT)
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
                .fidelityDiscountEligible(user.isDiscountActive())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status("ACTIVE")
                .imageUrl(user.getImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateUserRequestDTO dto, User user) {
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());
        if (dto.getUserType() != null) user.setUserType(UserType.valueOf(dto.getUserType()));
        if (dto.getAnnualVisits() != null) user.setAnnualVisits(dto.getAnnualVisits());
        if (dto.getRole() != null) user.setRole(resolveRole(dto.getRole()));
    }

    private Role resolveRole(String roleStr) {
        return Role.valueOf(roleStr.toUpperCase());
    }

    private UserType resolveUserType(String userTypeStr, boolean student) {
        if (userTypeStr != null) return UserType.valueOf(userTypeStr);
        return student ? UserType.STUDENT : UserType.ADULT;
    }
}
