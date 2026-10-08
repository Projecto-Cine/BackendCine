package com.cine.demo.service.impl;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.RegisterRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.model.Employee;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtUtil;
import com.cine.demo.service.AuthService;
import com.cine.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    /** Edad a partir de la cual corresponde la tarifa reducida de mayores. */
    private static final int SENIOR_AGE = 65;

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!isPasswordValid(dto.password(), user)) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return toLoginResponse(user, jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole()));
    }

    @Override
    public LoginResponseDTO employeeLogin(LoginRequestDTO dto) {
        Employee employee = employeeRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.password(), employee.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String roleDisplayName = employee.getRole().getDisplayName();
        String token = jwtUtil.generateToken(employee.getId(), employee.getEmail(), roleDisplayName);

        return LoginResponseDTO.builder()
                .token(token)
                .user(LoginResponseDTO.UserInfo.builder()
                        .id(employee.getId())
                        .name(employee.getName())
                        .email(employee.getEmail())
                        .role(roleDisplayName)
                        .status("ACTIVE")
                        .build())
                .build();
    }

    /**
     * Alta de cliente desde la tienda.
     *
     * El rol, el tipo de usuario y cualquier descuento se fijan aqui y nunca se
     * leen de la peticion: son decisiones del servidor. El tipo se deriva de la
     * fecha de nacimiento, que es obligatoria porque de ella depende el control
     * de calificacion por edad al comprar entradas.
     */
    @Override
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Ya existe una cuenta con el email " + email);
        }

        User user = User.builder()
                .name(dto.name().trim())
                .lastName(dto.lastName().trim())
                .email(email)
                .password(passwordEncoder.encode(dto.password()))
                .birthDate(dto.birthDate())
                .userType(userTypeFor(dto.birthDate()))
                .role(Role.CLIENT)
                .annualVisits(0)
                .discountActive(false)
                .build();

        User saved = userRepository.save(user);

        // El correo de bienvenida no debe hacer fallar el alta: la cuenta ya
        // existe y el cliente espera poder continuar su compra.
        try {
            emailService.sendMemberWelcome(saved.getEmail(), saved.getName());
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo de bienvenida a {}: {}", saved.getEmail(), e.getMessage());
        }

        return toLoginResponse(saved, jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole()));
    }

    /**
     * La condicion de estudiante no se deduce ni se autodeclara: supone una
     * tarifa reducida y la acredita el personal del cine.
     */
    private UserType userTypeFor(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= SENIOR_AGE
                ? UserType.SENIOR
                : UserType.ADULT;
    }

    private LoginResponseDTO toLoginResponse(User user, String token) {
        return LoginResponseDTO.builder()
                .token(token)
                .user(LoginResponseDTO.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .imageUrl(user.getImageUrl())
                        .status("ACTIVE")
                        .build())
                .build();
    }

    /**
     * Solo se aceptan contrasenas hasheadas con bcrypt.
     *
     * Antes habia un camino alternativo que comparaba la contrasena en claro
     * contra el valor almacenado y la migraba al vuelo. Eso convertia unas
     * credenciales en claro en base de datos en una via de login valida. La
     * migracion de datos sembrados ya la cubre DataInitializer, que rehashea
     * cualquier valor que no sea bcrypt al arrancar.
     */
    private boolean isPasswordValid(String rawPassword, User user) {
        String stored = user.getPassword();
        if (stored == null || stored.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, stored);
    }
}
