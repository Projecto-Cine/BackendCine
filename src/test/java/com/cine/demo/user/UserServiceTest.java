package com.cine.demo.user;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_returnsListOfUsers() {
        User user = User.builder().id(1L).name("Ana").build();
        UserResponseDTO dto = UserResponseDTO.builder().id(1L).name("Ana").build();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(dto);

        List<UserResponseDTO> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ana");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_throwsConflictException_whenEmailAlreadyExists() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana")
                .email("ana@test.com")
                .password("secret")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .build();
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ana@test.com");
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, UpdateUserRequestDTO.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}