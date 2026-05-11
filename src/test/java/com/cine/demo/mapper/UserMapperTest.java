package com.cine.demo.mapper;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_defaultsRoleToClient_whenRoleIsNull() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .nombre("Ana").email("ana@cine.com").password("p")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRol()).isEqualTo(Role.CLIENTE);
        assertThat(entity.getNombre()).isEqualTo("Ana");
        assertThat(entity.getEmail()).isEqualTo("ana@cine.com");
        assertThat(entity.getPassword()).isEqualTo("p");
    }

    @Test
    void toEntity_parsesRoleString_whenProvided() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .nombre("Admin").email("admin@cine.com").password("p")
                .fechaNacimiento(LocalDate.of(1980, 1, 1))
                .rol("ADMIN").userType("ADULT").visitasAnio(5).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRol()).isEqualTo(Role.ADMIN);
        assertThat(entity.getUserType()).isEqualTo(com.cine.demo.model.enums.UserType.ADULT);
        assertThat(entity.getVisitasAnio()).isEqualTo(5);
    }

    @Test
    void toResponseDto_serializesRoleAsString() {
        User user = User.builder()
                .id(1L).nombre("Ana").email("ana@cine.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .userType(com.cine.demo.model.enums.UserType.ADULT)
                .visitasAnio(3).rol(Role.CLIENTE)
                .imagenUrl("http://img/avatar.jpg").build();

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRol()).isEqualTo("CLIENTE");
        assertThat(dto.getImagenUrl()).isEqualTo("http://img/avatar.jpg");
        assertThat(dto.getVisitasAnio()).isEqualTo(3);
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        User existing = User.builder()
                .nombre("Ana").email("ana@old.com").password("OLD")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .userType(com.cine.demo.model.enums.UserType.ADULT)
                .visitasAnio(0).rol(Role.CLIENTE).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().nombre("Ana María").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getNombre()).isEqualTo("Ana María");
        assertThat(existing.getEmail()).isEqualTo("ana@old.com");
        assertThat(existing.getPassword()).isEqualTo("OLD");
        assertThat(existing.getRol()).isEqualTo(Role.CLIENTE);
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        User existing = User.builder()
                .nombre("X").email("x@x.com").password("p")
                .fechaNacimiento(LocalDate.of(1900, 1, 1))
                .userType(com.cine.demo.model.enums.UserType.ADULT)
                .visitasAnio(0).rol(Role.CLIENTE).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder()
                .nombre("Nuevo").email("nuevo@cine.com").password("nueva")
                .fechaNacimiento(LocalDate.of(2000, 5, 1))
                .userType("STUDENT").visitasAnio(7).rol("ADMIN").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getNombre()).isEqualTo("Nuevo");
        assertThat(existing.getEmail()).isEqualTo("nuevo@cine.com");
        assertThat(existing.getPassword()).isEqualTo("nueva");
        assertThat(existing.getFechaNacimiento()).isEqualTo(LocalDate.of(2000, 5, 1));
        assertThat(existing.getUserType()).isEqualTo(com.cine.demo.model.enums.UserType.STUDENT);
        assertThat(existing.getVisitasAnio()).isEqualTo(7);
        assertThat(existing.getRol()).isEqualTo(Role.ADMIN);
    }
}
