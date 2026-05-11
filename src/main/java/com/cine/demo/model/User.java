package com.cine.demo.model;

import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Builder.Default
    @Column(name = "visits_current_year")
    private int yearlyVisits = 0;

    @Builder.Default
    @Column(name = "discount_active")
    private boolean discountActive = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "role")
    private Role role = Role.CLIENT;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
