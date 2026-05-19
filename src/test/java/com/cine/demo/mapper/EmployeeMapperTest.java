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

    @Test
    void toEntity_mapsAllFields() {
        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .name("Carlos").email("carlos@lumen.com").role(EmployeeRole.CASHIER).build();

        Employee entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("Carlos");
        assertThat(entity.getEmail()).isEqualTo("carlos@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.CASHIER);
    }

    @Test
    void toEntity_idIsNull() {
        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .name("Ana").email("ana@lumen.com").role(EmployeeRole.CLEANING).build();

        assertThat(mapper.toEntity(dto).getId()).isNull();
    }

    @Test
    void toResponseDto_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Employee entity = Employee.builder()
                .id(1L).name("María").email("maria@lumen.com")
                .role(EmployeeRole.MANAGEMENT).createdAt(now).build();

        EmployeeResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("María");
        assertThat(dto.email()).isEqualTo("maria@lumen.com");
        assertThat(dto.role()).isEqualTo("GERENCIA");
        assertThat(dto.createdAt()).isEqualTo(now);
    }

    @Test
    void toResponseDto_roleIsNullWhenEntityRoleIsNull() {
        Employee entity = Employee.builder()
                .id(2L).name("Test").email("test@lumen.com").role(null).build();

        EmployeeResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.role()).isNull();
    }

    @Test
    void updateEntityFromDto_updatesAllNonNullFields() {
        Employee entity = Employee.builder()
                .id(1L).name("Old Name").email("old@lumen.com").role(EmployeeRole.CASHIER).build();
        UpdateEmployeeRequestDTO dto = UpdateEmployeeRequestDTO.builder()
                .name("New Name").email("new@lumen.com").role(EmployeeRole.MAINTENANCE).build();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("New Name");
        assertThat(entity.getEmail()).isEqualTo("new@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.MAINTENANCE);
    }

    @Test
    void updateEntityFromDto_doesNotOverwriteFieldsWhenDtoFieldsAreNull() {
        Employee entity = Employee.builder()
                .id(1L).name("Carlos").email("carlos@lumen.com").role(EmployeeRole.CASHIER).build();
        UpdateEmployeeRequestDTO dto = UpdateEmployeeRequestDTO.builder().build();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("Carlos");
        assertThat(entity.getEmail()).isEqualTo("carlos@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.CASHIER);
    }

    @Test
    void updateEntityFromDto_updatesOnlyNameWhenOnlyNameProvided() {
        Employee entity = Employee.builder()
                .name("Old").email("old@lumen.com").role(EmployeeRole.CLEANING).build();
        UpdateEmployeeRequestDTO dto = UpdateEmployeeRequestDTO.builder().name("New").build();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("New");
        assertThat(entity.getEmail()).isEqualTo("old@lumen.com");
        assertThat(entity.getRole()).isEqualTo(EmployeeRole.CLEANING);
    }
}
