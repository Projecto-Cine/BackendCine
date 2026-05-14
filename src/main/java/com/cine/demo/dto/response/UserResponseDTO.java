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
    private String username;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDate dateOfBirth;
    private String userType;
    private boolean student;
    private int annualVisits;
    private int visitsPerYear;
    private boolean discountActive;
    private boolean fidelityDiscountEligible;
    private String role;
    private String status;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
