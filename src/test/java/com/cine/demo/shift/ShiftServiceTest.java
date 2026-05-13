package com.cine.demo.shift;

import com.cine.demo.dto.request.ShiftRequestDTO;
import com.cine.demo.dto.request.UpdateShiftRequestDTO;
import com.cine.demo.dto.response.ShiftResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Employee;
import com.cine.demo.model.Shift;
import com.cine.demo.model.enums.EmployeeRole;
import com.cine.demo.model.enums.ShiftStatus;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.ShiftRepository;
import com.cine.demo.service.impl.ShiftServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock private ShiftRepository shiftRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    private Employee employee;
    private Shift shift;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().id(1L).name("Carlos").email("carlos@cine.com").role(EmployeeRole.CAJERO).build();
        shift = Shift.builder()
                .id(1L).employee(employee)
                .shiftDate(LocalDate.of(2026, 5, 13))
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(17, 0))
                .status(ShiftStatus.SCHEDULED).build();
    }

    @Test
    void findAll_returnsListOfShifts() {
        when(shiftRepository.findAll()).thenReturn(List.of(shift));

        List<ShiftResponseDTO> result = shiftService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Carlos");
        assertThat(result.get(0).getStatus()).isEqualTo("SCHEDULED");
    }

    @Test
    void findAll_returnsEmptyList_whenNoShifts() {
        when(shiftRepository.findAll()).thenReturn(List.of());

        assertThat(shiftService.findAll()).isEmpty();
    }

    @Test
    void findById_returnsShift_whenFound() {
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));

        ShiftResponseDTO result = shiftService.findById(1L);

        assertThat(result.getEmployeeId()).isEqualTo(1L);
        assertThat(result.getShiftDate()).isEqualTo(LocalDate.of(2026, 5, 13));
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(shiftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByDate_returnShiftsForDate() {
        LocalDate date = LocalDate.of(2026, 5, 13);
        when(shiftRepository.findByShiftDate(date)).thenReturn(List.of(shift));

        List<ShiftResponseDTO> result = shiftService.findByDate(date);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByDateRange_returnsShiftsInRange() {
        LocalDate from = LocalDate.of(2026, 5, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        when(shiftRepository.findByShiftDateBetween(from, to)).thenReturn(List.of(shift));

        List<ShiftResponseDTO> result = shiftService.findByDateRange(from, to);

        assertThat(result).hasSize(1);
    }

    @Test
    void save_throwsResourceNotFoundException_whenEmployeeNotFound() {
        ShiftRequestDTO dto = new ShiftRequestDTO();
        dto.setEmployeeId(99L);
        dto.setShiftDate(LocalDate.of(2026, 5, 13));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.save(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_persistsShiftWithDefaultStatus_whenStatusNotProvided() {
        ShiftRequestDTO dto = new ShiftRequestDTO();
        dto.setEmployeeId(1L);
        dto.setShiftDate(LocalDate.of(2026, 5, 13));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(shiftRepository.save(any(Shift.class))).thenReturn(shift);

        ShiftResponseDTO result = shiftService.save(dto);

        verify(shiftRepository).save(any(Shift.class));
        assertThat(result.getEmployeeName()).isEqualTo("Carlos");
    }

    @Test
    void save_usesProvidedStatus_whenStatusIsSet() {
        ShiftRequestDTO dto = new ShiftRequestDTO();
        dto.setEmployeeId(1L);
        dto.setShiftDate(LocalDate.of(2026, 5, 13));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        dto.setStatus("COMPLETED");
        Shift completed = Shift.builder().id(2L).employee(employee)
                .shiftDate(dto.getShiftDate()).startTime(dto.getStartTime()).endTime(dto.getEndTime())
                .status(ShiftStatus.COMPLETED).build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(shiftRepository.save(any(Shift.class))).thenReturn(completed);

        ShiftResponseDTO result = shiftService.save(dto);

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void update_throwsResourceNotFoundException_whenShiftNotFound() {
        when(shiftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.update(99L, new UpdateShiftRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesFieldsAndSaves() {
        UpdateShiftRequestDTO dto = new UpdateShiftRequestDTO();
        dto.setNotes("Evening shift");
        dto.setStatus("COMPLETED");
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftRepository.save(shift)).thenReturn(shift);

        shiftService.update(1L, dto);

        assertThat(shift.getNotes()).isEqualTo("Evening shift");
        assertThat(shift.getStatus()).isEqualTo(ShiftStatus.COMPLETED);
        verify(shiftRepository).save(shift);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(shiftRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> shiftService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesShift_whenExists() {
        when(shiftRepository.existsById(1L)).thenReturn(true);

        shiftService.delete(1L);

        verify(shiftRepository).deleteById(1L);
    }
}
