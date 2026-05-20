package com.cine.demo.incident;

import com.cine.demo.dto.request.IncidentRequestDTO;
import com.cine.demo.model.enums.IncidentStatus;
import com.cine.demo.dto.response.IncidentResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Incident;
import com.cine.demo.repository.IncidentRepository;
import com.cine.demo.service.impl.IncidentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    private Incident incident;

    @BeforeEach
    void setUp() {
        incident = Incident.builder()
                .id(1L).title("Door malfunction").description("Main door stuck")
                .severity("HIGH").status(IncidentStatus.OPEN).build();
    }

    @Test
    void findAll_returnsListOfIncidents() {
        when(incidentRepository.findAll()).thenReturn(List.of(incident));

        List<IncidentResponseDTO> result = incidentService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Door malfunction");
    }

    @Test
    void findAll_returnsEmptyList_whenNoIncidents() {
        when(incidentRepository.findAll()).thenReturn(List.of());

        assertThat(incidentService.findAll()).isEmpty();
    }

    @Test
    void findById_returnsIncident_whenFound() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        IncidentResponseDTO result = incidentService.findById(1L);

        assertThat(result.severity()).isEqualTo("HIGH");
        assertThat(result.resolved()).isFalse();
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_persistsAndReturnsIncident() {
        IncidentRequestDTO dto = IncidentRequestDTO.builder()
                .title("Door malfunction").description("Main door stuck")
                .severity("HIGH").build();
        when(incidentRepository.save(any(Incident.class))).thenReturn(incident);

        IncidentResponseDTO result = incidentService.save(dto);

        verify(incidentRepository).save(any(Incident.class));
        assertThat(result.title()).isEqualTo("Door malfunction");
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.update(99L, IncidentRequestDTO.builder().title("x").severity("LOW").build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesFieldsAndReturnsDTO() {
        IncidentRequestDTO dto = IncidentRequestDTO.builder()
                .title("Updated title").severity("LOW").status(IncidentStatus.RESOLVED).build();
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(incident)).thenReturn(incident);

        IncidentResponseDTO result = incidentService.update(1L, dto);

        assertThat(incident.getTitle()).isEqualTo("Updated title");
        assertThat(incident.getSeverity()).isEqualTo("LOW");
        assertThat(incident.getStatus()).isEqualTo(IncidentStatus.RESOLVED);
        verify(incidentRepository).save(incident);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(incidentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> incidentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesIncident_whenExists() {
        when(incidentRepository.existsById(1L)).thenReturn(true);

        incidentService.delete(1L);

        verify(incidentRepository).deleteById(1L);
    }
}
