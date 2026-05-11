package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String userType;
    private boolean student;
    private int yearlyVisits;
    private boolean discountActive;
    private String role;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
