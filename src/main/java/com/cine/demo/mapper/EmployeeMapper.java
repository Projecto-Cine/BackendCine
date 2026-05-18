package com.cine.demo.mapper;

import com.cine.demo.dto.request.EmployeeRequestDTO;
import com.cine.demo.dto.request.UpdateEmployeeRequestDTO;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import com.cine.demo.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDTO dto) {
        return Employee.builder()
                .name(dto.name())
                .email(dto.email())
                .role(dto.role())
                .phoneNumber(dto.phoneNumber())
                .build();
    }

    public EmployeeResponseDTO toResponseDto(Employee entity) {
        return EmployeeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole() != null ? entity.getRole().getDisplayName() : null)
                .phoneNumber(entity.getPhoneNumber())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(UpdateEmployeeRequestDTO dto, Employee entity) {
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.email() != null) entity.setEmail(dto.email());
        if (dto.role() != null) entity.setRole(dto.role());
        if (dto.phoneNumber() != null) entity.setPhoneNumber(dto.phoneNumber());
    }
}
