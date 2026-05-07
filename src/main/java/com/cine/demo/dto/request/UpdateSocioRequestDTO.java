package com.cine.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateSocioRequestDTO {

    private String membershipType;
    private String status;
    private LocalDate expiresAt;
    private String notes;
}