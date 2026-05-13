package com.cine.demo.employee;

import com.cine.demo.dto.request.EmployeeRequestDTO;
import com.cine.demo.dto.request.UpdateEmployeeRequestDTO;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.EmployeeMapper;
import com.cine.demo.model.Employee;
import com.cine.demo.model.enums.EmployeeRole;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.ShiftRepository;
import com.cine.demo.service.impl.EmployeeServiceImpl;
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
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private ShiftRepository shiftRepository;
    @Mock private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L).name("Carlos").email("carlos@cine.com").role(EmployeeRole.CASHIER).build();
        responseDTO = EmployeeResponseDTO.builder()
                .id(1L).name("Carlos").email("carlos@cine.com").role("CAJERO").build();
    }

    @Test
    void findAll_returnsListOfEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeMapper.toResponseDto(employee)).thenReturn(responseDTO);

        List<EmployeeResponseDTO> result = employeeService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos");
    }

    @Test
    void findAll_returnsEmptyList_whenNoEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        assertThat(employeeService.findAll()).isEmpty();
    }

    @Test
    void findById_returnsEmployee_whenFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDto(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.findById(1L);

        assertThat(result.getEmail()).isEqualTo("carlos@cine.com");
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_throwsConflictException_whenEmailAlreadyExists() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Carlos"); dto.setEmail("carlos@cine.com"); dto.setRole(EmployeeRole.CASHIER);
        when(employeeRepository.existsByEmail("carlos@cine.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.save(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("carlos@cine.com");
    }

    @Test
    void save_persistsAndReturnsEmployee_whenEmailNew() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Carlos"); dto.setEmail("carlos@cine.com"); dto.setRole(EmployeeRole.CASHIER);
        when(employeeRepository.existsByEmail("carlos@cine.com")).thenReturn(false);
        when(employeeMapper.toEntity(dto)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponseDto(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.save(dto);

        verify(employeeRepository).save(employee);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(99L, new UpdateEmployeeRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_throwsConflictException_whenChangingToEmailInUse() {
        UpdateEmployeeRequestDTO dto = new UpdateEmployeeRequestDTO();
        dto.setEmail("taken@cine.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail("taken@cine.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.update(1L, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("taken@cine.com");
    }

    @Test
    void update_updatesEmployee_whenValid() {
        UpdateEmployeeRequestDTO dto = new UpdateEmployeeRequestDTO();
        dto.setName("Carlos Updated");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponseDto(employee)).thenReturn(responseDTO);

        employeeService.update(1L, dto);

        verify(employeeMapper).updateEntityFromDto(dto, employee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_throwsConflictException_whenEmployeeHasShifts() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(shiftRepository.existsByEmployeeId(1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.delete(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("shifts");
    }

    @Test
    void delete_deletesEmployee_whenNoShifts() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(shiftRepository.existsByEmployeeId(1L)).thenReturn(false);

        employeeService.delete(1L);

        verify(employeeRepository).deleteById(1L);
    }
}
