package com.cine.demo.service.impl;

import com.cine.demo.dto.request.IncidentRequestDTO;
import com.cine.demo.dto.response.AssignedEmployeeDTO;
import com.cine.demo.dto.response.IncidentResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Employee;
import com.cine.demo.model.Incident;
import com.cine.demo.model.enums.EmployeeRole;
import com.cine.demo.model.enums.IncidentStatus;
import com.cine.demo.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;

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
        Employee assignedTo = resolveAssignedEmployee(dto.assignedTo());
        Incident incident = Incident.builder()
                .title(dto.title())
                .description(dto.description())
                .severity(dto.severity())
                .category(dto.category())
                .room(dto.room())
                .status(dto.status() != null ? dto.status() : IncidentStatus.OPEN)
                .assignedTo(assignedTo)
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
        if (dto.status() != null) incident.setStatus(dto.status());
        if (dto.assignedTo() != null) {
            incident.setAssignedTo(resolveAssignedEmployee(dto.assignedTo()));
        } else {
            incident.setAssignedTo(null);
        }
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
        AssignedEmployeeDTO assignedDto = i.getAssignedTo() != null
                ? AssignedEmployeeDTO.builder()
                        .id(i.getAssignedTo().getId())
                        .name(i.getAssignedTo().getName())
                        .build()
                : null;
        return IncidentResponseDTO.builder()
                .id(i.getId())
                .title(i.getTitle())
                .description(i.getDescription())
                .severity(i.getSeverity())
                .category(i.getCategory())
                .room(i.getRoom())
                .status(i.getStatus())
                .resolved(i.getStatus() == IncidentStatus.RESOLVED)
                .assignedTo(assignedDto)
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }

    private Employee resolveAssignedEmployee(Long employeeId) {
        if (employeeId == null) return null;
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        if (employee.getRole() != EmployeeRole.MAINTENANCE && employee.getRole() != EmployeeRole.CLEANING) {
            throw new com.cine.demo.exception.BusinessRuleException(
                    "Solo se puede asignar a empleados de MANTENIMIENTO o LIMPIEZA");
        }
        return employee;
    }
}
