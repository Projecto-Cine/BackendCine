package com.cine.demo.service.impl;

import com.cine.demo.dto.request.UpdateWorkerRequestDTO;
import com.cine.demo.dto.request.WorkerRequestDTO;
import com.cine.demo.dto.response.WorkerResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerServiceImpl implements WorkerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<WorkerResponseDTO> getAll() {
        return userRepository.findByRoleNot(Role.CLIENT).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerResponseDTO getById(Long id) {
        return toDto(findWorkerOrThrow(id));
    }

    @Override
    public WorkerResponseDTO create(WorkerRequestDTO dto) {
        Role role = parseWorkerRole(dto.getRole());

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        String username = generateUsername(dto.getName(), dto.getEmail());

        User worker = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .username(username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(role)
                .status(dto.getStatus() != null ? dto.getStatus() : "active")
                .dateOfBirth(dto.getDateOfBirth())
                .build();

        return toDto(userRepository.save(worker));
    }

    @Override
    public WorkerResponseDTO update(Long id, UpdateWorkerRequestDTO dto) {
        User worker = findWorkerOrThrow(id);

        if (dto.getName() != null) worker.setName(dto.getName());
        if (dto.getEmail() != null && !dto.getEmail().equals(worker.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictException("Ya existe un usuario con el email: " + dto.getEmail());
            }
            worker.setEmail(dto.getEmail());
        }
        if (dto.getRole() != null) {
            worker.setRole(parseWorkerRole(dto.getRole()));
        }
        if (dto.getDateOfBirth() != null) worker.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getStatus() != null) worker.setStatus(dto.getStatus());

        return toDto(userRepository.save(worker));
    }

    @Override
    public void delete(Long id, Long requesterId) {
        User worker = findWorkerOrThrow(id);
        if (worker.getId().equals(requesterId)) {
            throw new ConflictException("No puedes eliminar tu propio usuario");
        }
        userRepository.delete(worker);
    }

    private User findWorkerOrThrow(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con id: " + id));
        if (user.getRole() == Role.CLIENT) {
            throw new ResourceNotFoundException("Trabajador no encontrado con id: " + id);
        }
        return user;
    }

    private Role parseWorkerRole(String roleStr) {
        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new ConflictException("Rol no válido: " + roleStr);
        }
        if (role == Role.CLIENT) {
            throw new ConflictException("El rol CLIENT no está permitido para trabajadores");
        }
        return role;
    }

    private String generateUsername(String name, String email) {
        String base = name.toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z0-9.]", "");
        if (base.isBlank()) {
            base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        }
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }

    private WorkerResponseDTO toDto(User user) {
        return WorkerResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}