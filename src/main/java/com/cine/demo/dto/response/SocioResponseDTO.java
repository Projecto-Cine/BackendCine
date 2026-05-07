package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SocioResponseDTO {
    private Long id;
    private Long clientId;
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String membershipNumber;
    private String membershipType;
    private int discountPct;
    private String status;
    private LocalDateTime joinedAt;
    private LocalDate expiresAt;
    private String notes;
}