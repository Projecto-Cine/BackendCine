package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateWorkerRequestDTO {

    private String name;

    @Email
    private String email;

    private String role;

    private LocalDate dateOfBirth;

    private String status;
}