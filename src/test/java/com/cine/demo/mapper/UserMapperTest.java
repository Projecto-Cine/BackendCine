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
                .name("Ana").email("ana@cine.com").password("p")
                .birthDate(LocalDate.of(1990, 1, 1)).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRole()).isEqualTo(Role.CLIENT);
        assertThat(entity.getName()).isEqualTo("Ana");
        assertThat(entity.getEmail()).isEqualTo("ana@cine.com");
        assertThat(entity.getPassword()).isEqualTo("p");
    }

    @Test
    void toEntity_parsesRoleString_whenProvided() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Admin").email("admin@cine.com").password("p")
                .birthDate(LocalDate.of(1980, 1, 1))
                .role("ADMIN").userType("ADULT").annualVisits(5).build();

        User entity = mapper.toEntity(dto);

        assertThat(entity.getRole()).isEqualTo(Role.ADMIN);
        assertThat(entity.getUserType()).isEqualTo(com.cine.demo.model.enums.UserType.ADULT);
        assertThat(entity.getAnnualVisits()).isEqualTo(5);
    }

    @Test
    void toResponseDto_serializesRoleAsString() {
        User user = User.builder()
                .id(1L).name("Ana").email("ana@cine.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(com.cine.demo.model.enums.UserType.ADULT)
                .annualVisits(3).role(Role.CLIENT)
                .imageUrl("http://img/avatar.jpg").build();

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRole()).isEqualTo("CLIENT");
        assertThat(dto.getImageUrl()).isEqualTo("http://img/avatar.jpg");
        assertThat(dto.getAnnualVisits()).isEqualTo(3);
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        User existing = User.builder()
                .name("Ana").email("ana@old.com").password("OLD")
                .birthDate(LocalDate.of(1990, 1, 1))
                .userType(com.cine.demo.model.enums.UserType.ADULT)
                .annualVisits(0).role(Role.CLIENT).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().name("Ana Maria").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("Ana Maria");
        assertThat(existing.getEmail()).isEqualTo("ana@old.com");
        assertThat(existing.getPassword()).isEqualTo("OLD");
        assertThat(existing.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        User existing = User.builder()
                .name("X").email("x@x.com").password("p")
                .birthDate(LocalDate.of(1900, 1, 1))
                .userType(com.cine.demo.model.enums.UserType.ADULT)
                .annualVisits(0).role(Role.CLIENT).build();
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder()
                .name("Nuevo").email("nuevo@cine.com").password("nueva")
                .birthDate(LocalDate.of(2000, 5, 1))
                .userType("STUDENT").annualVisits(7).role("ADMIN").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("Nuevo");
        assertThat(existing.getEmail()).isEqualTo("nuevo@cine.com");
        assertThat(existing.getPassword()).isEqualTo("nueva");
        assertThat(existing.getBirthDate()).isEqualTo(LocalDate.of(2000, 5, 1));
        assertThat(existing.getUserType()).isEqualTo(com.cine.demo.model.enums.UserType.STUDENT);
        assertThat(existing.getAnnualVisits()).isEqualTo(7);
        assertThat(existing.getRole()).isEqualTo(Role.ADMIN);
    }
}
