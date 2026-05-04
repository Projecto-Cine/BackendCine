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
    private String email;
    private LocalDate dateOfBirth;
    private boolean student;
    private int visitsPerYear;
    private String role;
    private String status;
    private String imageUrl;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}