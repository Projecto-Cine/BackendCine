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

    /**
     * Si el DTO no especifica rol, el mapeo a entidad debe asignar
     * rol = CLIENTE por defecto. Esto evita que un usuario sin rol
     * acabe con un valor inválido.
     */
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

    /**
     * Si el DTO incluye un rol válido como cadena, debe parsearse al enum.
     * Cubre el caso de creación de un administrador desde la API.
     */
    @Test
    void toEntity_parsesRoleString_whenProvided() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .nombre("Admin").email("admin@cine.com").password("p")
                .fechaNacimiento(LocalDate.of(1980, 1, 1))
                .rol("ADMIN").esEstudiante(true).visitasAnio(5).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRol()).isEqualTo(Role.ADMIN);
        assertThat(entity.isEsEstudiante()).isTrue();
        assertThat(entity.getVisitasAnio()).isEqualTo(5);
    }

    /**
     * Conversión entidad → DTO: el rol enum debe serializarse como string
     * para que el JSON de salida sea legible para el frontend.
     */
    @Test
    void toResponseDto_serializesRoleAsString() {
        User user = User.builder()
                .id(1L).nombre("Ana").email("ana@cine.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .esEstudiante(false).visitasAnio(3).rol(Role.CLIENTE)
                .imagenUrl("http://img/avatar.jpg").build();

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRol()).isEqualTo("CLIENTE");
        assertThat(dto.getImagenUrl()).isEqualTo("http://img/avatar.jpg");
        assertThat(dto.getVisitasAnio()).isEqualTo(3);
    }

    /**
     * Patch parcial: si sólo enviamos nombre, el resto de campos
     * (email, contraseña, etc.) deben permanecer intactos.
     */
    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        User existing = User.builder()
                .nombre("Ana").email("ana@old.com").password("OLD")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .esEstudiante(false).visitasAnio(0).rol(Role.CLIENTE).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().nombre("Ana María").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getNombre()).isEqualTo("Ana María");
        assertThat(existing.getEmail()).isEqualTo("ana@old.com");
        assertThat(existing.getPassword()).isEqualTo("OLD");
        assertThat(existing.getRol()).isEqualTo(Role.CLIENTE);
    }

    /**
     * Si el DTO trae todos los campos, todos se actualizan.
     * Útil para confirmar que la lógica del mapper recorre todas las ramas.
     */
    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        User existing = User.builder()
                .nombre("X").email("x@x.com").password("p")
                .fechaNacimiento(LocalDate.of(1900, 1, 1))
                .esEstudiante(false).visitasAnio(0).rol(Role.CLIENTE).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder()
                .nombre("Nuevo").email("nuevo@cine.com").password("nueva")
                .fechaNacimiento(LocalDate.of(2000, 5, 1))
                .esEstudiante(true).visitasAnio(7).rol("ADMIN").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getNombre()).isEqualTo("Nuevo");
        assertThat(existing.getEmail()).isEqualTo("nuevo@cine.com");
        assertThat(existing.getPassword()).isEqualTo("nueva");
        assertThat(existing.getFechaNacimiento()).isEqualTo(LocalDate.of(2000, 5, 1));
        assertThat(existing.isEsEstudiante()).isTrue();
        assertThat(existing.getVisitasAnio()).isEqualTo(7);
        assertThat(existing.getRol()).isEqualTo(Role.ADMIN);
    }
}
