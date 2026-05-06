package com.cine.demo.service.impl;

import com.cine.demo.dto.response.ClientSummaryDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private static final int FIDELITY_THRESHOLD = 10;

    private final UserRepository userRepository;

    @Override
    public List<ClientSummaryDTO> getAll() {
        return userRepository.findByRole(Role.CLIENT).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ClientSummaryDTO getById(Long id) {
        User user = userRepository.findByIdAndRole(id, Role.CLIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return toDto(user);
    }

    @Override
    public List<ClientSummaryDTO> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return userRepository.searchClients(Role.CLIENT, query.trim()).stream()
                .map(this::toDto)
                .toList();
    }

    private ClientSummaryDTO toDto(User user) {
        return ClientSummaryDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .student(user.isStudent())
                .visitsPerYear(user.getVisitsPerYear())
                .fidelityDiscountEligible(user.getVisitsPerYear() >= FIDELITY_THRESHOLD)
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}