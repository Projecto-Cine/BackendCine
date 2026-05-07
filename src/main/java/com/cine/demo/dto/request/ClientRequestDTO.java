package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientRequestDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String phone;

    private LocalDate dateOfBirth;
}