package com.cine.demo.service.impl;

import com.cine.demo.dto.request.IncidentRequestDTO;
import com.cine.demo.dto.response.IncidentResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Incident;
import com.cine.demo.repository.IncidentRepository;
import com.cine.demo.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public List<IncidentResponseDTO> findAll() {
        return incidentRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public IncidentResponseDTO findById(Long id) {
        return incidentRepository.findById(id)
                .map(this::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }

    @Override
    @Transactional
    public IncidentResponseDTO save(IncidentRequestDTO dto) {
        Incident incident = Incident.builder()
                .title(dto.title())
                .description(dto.description())
                .severity(dto.severity())
                .category(dto.category())
                .room(dto.room())
                .resolved(dto.resolved())
                .build();
        return toResponseDto(incidentRepository.save(incident));
    }

    @Override
    @Transactional
    public IncidentResponseDTO update(Long id, IncidentRequestDTO dto) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
        if (dto.title() != null) incident.setTitle(dto.title());
        if (dto.description() != null) incident.setDescription(dto.description());
        if (dto.severity() != null) incident.setSeverity(dto.severity());
        if (dto.category() != null) incident.setCategory(dto.category());
        if (dto.room() != null) incident.setRoom(dto.room());
        incident.setResolved(dto.resolved());
        return toResponseDto(incidentRepository.save(incident));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incident not found with id: " + id);
        }
        incidentRepository.deleteById(id);
    }

    private IncidentResponseDTO toResponseDto(Incident i) {
        return IncidentResponseDTO.builder()
                .id(i.getId())
                .title(i.getTitle())
                .description(i.getDescription())
                .severity(i.getSeverity())
                .category(i.getCategory())
                .room(i.getRoom())
                .resolved(i.isResolved())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
