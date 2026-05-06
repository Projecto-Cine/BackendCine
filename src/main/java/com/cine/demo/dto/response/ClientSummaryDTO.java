package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ClientSummaryDTO {
    private Long id;
    private String name;
    private String email;
    private String username;
    private boolean student;
    private int visitsPerYear;
    private boolean fidelityDiscountEligible;
    private String status;
    private LocalDate dateOfBirth;
}