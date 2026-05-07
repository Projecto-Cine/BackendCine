package com.cine.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SocioRequestDTO {

    // Si el cliente ya existe
    private Long clientId;

    // Si hay que crear el cliente al mismo tiempo
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;

    private String membershipType;

    private LocalDate expiresAt;

    private String notes;
}