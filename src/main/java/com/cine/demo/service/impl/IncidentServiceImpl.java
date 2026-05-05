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
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getAll() {
        return incidentRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentResponseDTO getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Override
    public IncidentResponseDTO create(IncidentRequestDTO dto) {
        Incident incident = Incident.builder()
                .title(dto.getTitle() != null ? dto.getTitle() : "Sin título")
                .category(dto.getCategory())
                .priority(dto.getPriority() != null ? dto.getPriority() : "medium")
                .status(dto.getStatus() != null ? dto.getStatus() : "open")
                .room(dto.getRoom())
                .description(dto.getDescription())
                .assignedTo(dto.getAssignedTo())
                .reportedBy(dto.getReportedBy() != null ? dto.getReportedBy() : "Sistema")
                .build();
        return toDto(incidentRepository.save(incident));
    }

    @Override
    public IncidentResponseDTO update(Long id, IncidentRequestDTO dto) {
        Incident incident = findOrThrow(id);
        if (dto.getTitle() != null) incident.setTitle(dto.getTitle());
        if (dto.getCategory() != null) incident.setCategory(dto.getCategory());
        if (dto.getPriority() != null) incident.setPriority(dto.getPriority());
        if (dto.getStatus() != null) incident.setStatus(dto.getStatus());
        if (dto.getRoom() != null) incident.setRoom(dto.getRoom());
        if (dto.getDescription() != null) incident.setDescription(dto.getDescription());
        if (dto.getAssignedTo() != null) incident.setAssignedTo(dto.getAssignedTo());
        if (dto.getReportedBy() != null) incident.setReportedBy(dto.getReportedBy());
        return toDto(incidentRepository.save(incident));
    }

    @Override
    public void delete(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incidencia no encontrada con id: " + id);
        }
        incidentRepository.deleteById(id);
    }

    private Incident findOrThrow(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));
    }

    private IncidentResponseDTO toDto(Incident incident) {
        return IncidentResponseDTO.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .category(incident.getCategory())
                .priority(incident.getPriority())
                .status(incident.getStatus())
                .room(incident.getRoom())
                .description(incident.getDescription())
                .assignedTo(incident.getAssignedTo())
                .reportedBy(incident.getReportedBy())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}