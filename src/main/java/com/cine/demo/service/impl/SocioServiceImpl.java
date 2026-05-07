package com.cine.demo.service.impl;

import com.cine.demo.dto.request.SocioRequestDTO;
import com.cine.demo.dto.request.UpdateSocioRequestDTO;
import com.cine.demo.dto.response.SocioResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Socio;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.MembershipStatus;
import com.cine.demo.model.enums.MembershipType;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.SocioRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SocioServiceImpl implements SocioService {

    private final SocioRepository socioRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<SocioResponseDTO> getAll() {
        return socioRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SocioResponseDTO getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocioResponseDTO> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        return socioRepository.search(query.trim()).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public SocioResponseDTO create(SocioRequestDTO dto) {
        User client;

        if (dto.getClientId() != null) {
            client = userRepository.findByIdAndRole(dto.getClientId(), Role.CLIENT)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + dto.getClientId()));
        } else {
            if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                throw new ConflictException("Se requiere clientId o email para crear un socio");
            }
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictException("Ya existe un usuario con el email: " + dto.getEmail());
            }
            String username = generateUsername(dto.getName(), dto.getEmail());
            client = userRepository.save(User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .username(username)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .phone(dto.getPhone())
                    .dateOfBirth(dto.getDateOfBirth())
                    .role(Role.CLIENT)
                    .build());
        }

        if (socioRepository.existsByClientId(client.getId())) {
            throw new ConflictException("El cliente ya tiene una membresía activa");
        }

        MembershipType type = parseMembershipType(dto.getMembershipType());

        client.setSocio(true);
        client.setSocioSince(LocalDate.now());
        userRepository.save(client);

        Socio socio = Socio.builder()
                .client(client)
                .membershipNumber(generateMembershipNumber())
                .membershipType(type)
                .discountPct(discountFor(type))
                .expiresAt(dto.getExpiresAt())
                .notes(dto.getNotes())
                .build();

        return toDto(socioRepository.save(socio));
    }

    @Override
    public SocioResponseDTO update(Long id, UpdateSocioRequestDTO dto) {
        Socio socio = findOrThrow(id);

        if (dto.getMembershipType() != null) {
            MembershipType type = parseMembershipType(dto.getMembershipType());
            socio.setMembershipType(type);
            socio.setDiscountPct(discountFor(type));
        }
        if (dto.getStatus() != null) {
            try {
                socio.setStatus(MembershipStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new ConflictException("Estado no válido: " + dto.getStatus());
            }
        }
        if (dto.getExpiresAt() != null) socio.setExpiresAt(dto.getExpiresAt());
        if (dto.getNotes() != null) socio.setNotes(dto.getNotes());

        return toDto(socioRepository.save(socio));
    }

    @Override
    public void delete(Long id) {
        Socio socio = findOrThrow(id);
        User client = socio.getClient();
        client.setSocio(false);
        userRepository.save(client);
        socioRepository.delete(socio);
    }

    private Socio findOrThrow(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con id: " + id));
    }

    private MembershipType parseMembershipType(String value) {
        if (value == null) return MembershipType.BASIC;
        try {
            return MembershipType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConflictException("Tipo de membresía no válido: " + value);
        }
    }

    private int discountFor(MembershipType type) {
        return switch (type) {
            case BASIC -> 10;
            case PREMIUM -> 20;
            case VIP -> 30;
        };
    }

    private String generateMembershipNumber() {
        long count = socioRepository.count() + 1;
        return String.format("MBR-%06d", count);
    }

    private String generateUsername(String name, String email) {
        String base = name == null ? "" : name.toLowerCase()
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

    private SocioResponseDTO toDto(Socio socio) {
        User client = socio.getClient();
        return SocioResponseDTO.builder()
                .id(socio.getId())
                .clientId(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .dateOfBirth(client.getDateOfBirth())
                .membershipNumber(socio.getMembershipNumber())
                .membershipType(socio.getMembershipType().name())
                .discountPct(socio.getDiscountPct())
                .status(socio.getStatus().name())
                .joinedAt(socio.getJoinedAt())
                .expiresAt(socio.getExpiresAt())
                .notes(socio.getNotes())
                .build();
    }
}