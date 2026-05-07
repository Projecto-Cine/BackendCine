package com.cine.demo.user;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(1L).name("Ana").email("ana@test.com")
                .password("ENCODED").birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.CLIENTE).build();
    }

    @Test
    void getAll_returnsListOfUsers() {
        UserResponseDTO dto = UserResponseDTO.builder().id(1L).name("Ana").build();
        when(userRepository.findAll()).thenReturn(List.of(existingUser));
        when(userMapper.toResponseDto(existingUser)).thenReturn(dto);

        List<UserResponseDTO> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ana");
    }

    @Test
    void getById_returnsUser_whenFound() {
        UserResponseDTO dto = UserResponseDTO.builder().id(1L).name("Ana").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userMapper.toResponseDto(existingUser)).thenReturn(dto);

        UserResponseDTO result = userService.getById(1L);

        assertThat(result.getName()).isEqualTo("Ana");
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
                .name("Ana").email("ana@test.com").password("secret")
                .birthDate(LocalDate.of(1995, 1, 1)).build();
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ana@test.com");
    }

    @Test
    void create_encodesPasswordAndPersistsUser_whenEmailNew() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Nueva").email("nueva@test.com").password("plain")
                .birthDate(LocalDate.of(2000, 1, 1)).build();
        User entityFromMapper = User.builder().name("Nueva").email("nueva@test.com")
                .password("plain").role(Role.CLIENTE).build();
        User saved = User.builder().id(2L).name("Nueva").email("nueva@test.com")
                .password("BCRYPT").role(Role.CLIENTE).build();
        when(userRepository.existsByEmail("nueva@test.com")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(entityFromMapper);
        when(passwordEncoder.encode("plain")).thenReturn("BCRYPT");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponseDto(saved))
                .thenReturn(UserResponseDTO.builder().id(2L).name("Nueva").build());

        UserResponseDTO result = userService.create(dto);

        verify(userRepository).save(argThat(u -> "BCRYPT".equals(u.getPassword())));
        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, UpdateUserRequestDTO.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_throwsConflictException_whenChangingToEmailInUseByAnother() {
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().email("taken@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("taken@test.com");
    }

    @Test
    void update_encodesNewPassword_whenProvided() {
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().password("newPass").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPass")).thenReturn("NEW_BCRYPT");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toResponseDto(existingUser))
                .thenReturn(UserResponseDTO.builder().id(1L).build());

        userService.update(1L, dto);

        verify(passwordEncoder).encode("newPass");
        assertThat(existingUser.getPassword()).isEqualTo("NEW_BCRYPT");
    }

    @Test
    void update_doesNotEncodePassword_whenBlank() {
        UpdateUserRequestDTO dto = UpdateUserRequestDTO.builder().password("   ").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toResponseDto(existingUser))
                .thenReturn(UserResponseDTO.builder().id(1L).build());

        userService.update(1L, dto);

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_callsRepository_whenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void uploadImage_throwsResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.uploadImage(99L,
                new MockMultipartFile("file", new byte[]{1, 2, 3})))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void uploadImage_setsImageUrlAndSaves_whenUserFound() {
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "image/png", new byte[]{1});
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(cloudinaryService.uploadImage(file, "users")).thenReturn("https://cdn/img.png");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toResponseDto(existingUser))
                .thenReturn(UserResponseDTO.builder().id(1L).build());

        userService.uploadImage(1L, file);

        assertThat(existingUser.getImageUrl()).isEqualTo("https://cdn/img.png");
        verify(userRepository).save(existingUser);
    }
}