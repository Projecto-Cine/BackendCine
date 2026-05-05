package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String role;
    private String status;
}