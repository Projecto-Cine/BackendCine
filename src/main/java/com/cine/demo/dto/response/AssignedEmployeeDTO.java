package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record AssignedEmployeeDTO(
        Long id,
        String name
) {}