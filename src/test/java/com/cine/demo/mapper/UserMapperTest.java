package com.cine.demo.mapper;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_defaultsRoleToClient_whenRoleIsNull() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana").email("ana@cine.com").password("p")
                .birthDate(LocalDate.of(1990, 1, 1)).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRole()).isEqualTo(Role.CLIENTE);
        assertThat(entity.getName()).isEqualTo("Ana");
        assertThat(entity.getEmail()).isEqualTo("ana@cine.com");
        assertThat(entity.getPassword()).isEqualTo("p");
    }

    @Test
    void toEntity_parsesRoleString_whenProvided() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Admin").email("admin@cine.com").password("p")
                .birthDate(LocalDate.of(1980, 1, 1))
                .role("ADMIN").student(true).visitsCurrentYear(5).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRole()).isEqualTo(Role.ADMIN);
        assertThat(entity.getVisitsCurrentYear()).isEqualTo(5);
    }

    @Test
    void toResponseDto_serializesRoleAsString() {
        User user = User.builder()
                .id(1L).name("Ana").email("ana@cine.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(UserType.ADULT)
                .visitsCurrentYear(3).role(Role.CLIENTE)
                .imageUrl("http://img/avatar.jpg").build();

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRole()).isEqualTo("CLIENTE");
        assertThat(dto.getImageUrl()).isEqualTo("http://img/avatar.jpg");
        assertThat(dto.getVisitsCurrentYear()).isEqualTo(3);
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        User existing = User.builder()
                .name("Ana").email("ana@old.com").password("OLD")
                .birthDate(LocalDate.of(1990, 1, 1))
                .visitsCurrentYear(0).role(Role.CLIENTE).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().name("Ana María").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("Ana María");
        assertThat(existing.getEmail()).isEqualTo("ana@old.com");
        assertThat(existing.getPassword()).isEqualTo("OLD");
        assertThat(existing.getRole()).isEqualTo(Role.CLIENTE);
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        User existing = User.builder()
                .name("X").email("x@x.com").password("p")
                .birthDate(LocalDate.of(1900, 1, 1))
                .visitsCurrentYear(0).role(Role.CLIENTE).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder()
                .name("Nuevo").email("nuevo@cine.com").password("nueva")
                .birthDate(LocalDate.of(2000, 5, 1))
                .visitsCurrentYear(7).role("ADMIN").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("Nuevo");
        assertThat(existing.getEmail()).isEqualTo("nuevo@cine.com");
        assertThat(existing.getPassword()).isEqualTo("nueva");
        assertThat(existing.getBirthDate()).isEqualTo(LocalDate.of(2000, 5, 1));
        assertThat(existing.getVisitsCurrentYear()).isEqualTo(7);
        assertThat(existing.getRole()).isEqualTo(Role.ADMIN);
    }
}