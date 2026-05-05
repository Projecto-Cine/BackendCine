package com.cine.demo.dto.response;

import com.cine.demo.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private Role rol;
    private String imagenUrl;
}
