package com.cine.demo.model;

import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2)
    @Column(name = "name")
    private String nombre;

    @Column(name = "last_name")
    private String lastName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @Column(name = "birth_date")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Builder.Default
    @Column(name = "visits_current_year")
    private int visitasAnio = 0;

    @Builder.Default
    @Column(name = "discount_active")
    private boolean discountActive = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role rol = Role.CLIENTE;

    @Column(name = "image_url")
    private String imagenUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
