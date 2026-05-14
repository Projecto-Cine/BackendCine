package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    @Size(min = 2, message = "Name must have at least 2 characters")
    private String name;

    private String lastName;

    @Email(message = "Email format is invalid")
    private String email;

    private String password;
    private LocalDate birthDate;
    private String userType;
    private Boolean student;
    private Integer annualVisits;
    private String role;
}
