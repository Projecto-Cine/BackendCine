package com.cine.demo.mapper;

import com.cine.demo.dto.request.EmployeeRequestDTO;
import com.cine.demo.dto.request.UpdateEmployeeRequestDTO;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import com.cine.demo.model.Employee;
import com.cine.demo.model.enums.EmployeeRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = new EmployeeMapper();

    // ── toEntity ──────────────────────────────────────────────────────────

    @Test
    void toEntity_mapsAllFields() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Carlos");
        dto.setEmail("carlos@lumen.com");
        dto.setRole(EmployeeRole.CAJERO);

        Employee entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("Carlos");
        assertThat(entity.getEmail()).isEqualTo("carlos@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.CAJERO);
    }

    @Test
    void toEntity_idIsNull() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Ana");
        dto.setEmail("ana@lumen.com");
        dto.setRole(EmployeeRole.LIMPIEZA);

        assertThat(mapper.toEntity(dto).getId()).isNull();
    }

    // ── toResponseDto ─────────────────────────────────────────────────────

    @Test
    void toResponseDto_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Employee entity = Employee.builder()
                .id(1L).name("María").email("maria@lumen.com")
                .role(EmployeeRole.GERENCIA).createdAt(now).build();

        EmployeeResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("María");
        assertThat(dto.getEmail()).isEqualTo("maria@lumen.com");
        assertThat(dto.getRole()).isEqualTo("GERENCIA");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toResponseDto_roleIsNullWhenEntityRoleIsNull() {
        Employee entity = Employee.builder()
                .id(2L).name("Test").email("test@lumen.com").role(null).build();

        EmployeeResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.getRole()).isNull();
    }

    // ── updateEntityFromDto ───────────────────────────────────────────────

    @Test
    void updateEntityFromDto_updatesAllNonNullFields() {
        Employee entity = Employee.builder()
                .id(1L).name("Old Name").email("old@lumen.com").role(EmployeeRole.CAJERO).build();
        UpdateEmployeeRequestDTO dto = new UpdateEmployeeRequestDTO();
        dto.setName("New Name");
        dto.setEmail("new@lumen.com");
        dto.setRole(EmployeeRole.SEGURIDAD);

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("New Name");
        assertThat(entity.getEmail()).isEqualTo("new@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.SEGURIDAD);
    }

    @Test
    void updateEntityFromDto_doesNotOverwriteFieldsWhenDtoFieldsAreNull() {
        Employee entity = Employee.builder()
                .id(1L).name("Carlos").email("carlos@lumen.com").role(EmployeeRole.CAJERO).build();
        UpdateEmployeeRequestDTO dto = new UpdateEmployeeRequestDTO();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("Carlos");
        assertThat(entity.getEmail()).isEqualTo("carlos@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.CAJERO);
    }

    @Test
    void updateEntityFromDto_updatesOnlyNameWhenOnlyNameProvided() {
        Employee entity = Employee.builder()
                .name("Old").email("old@lumen.com").role(EmployeeRole.LIMPIEZA).build();
        UpdateEmployeeRequestDTO dto = new UpdateEmployeeRequestDTO();
        dto.setName("New");

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("New");
        assertThat(entity.getEmail()).isEqualTo("old@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.LIMPIEZA);
    }
}
