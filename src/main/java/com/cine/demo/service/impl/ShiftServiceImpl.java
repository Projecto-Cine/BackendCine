package com.cine.demo.service.impl;

import com.cine.demo.dto.request.ShiftRequestDTO;
import com.cine.demo.dto.request.UpdateShiftRequestDTO;
import com.cine.demo.dto.response.ShiftResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Employee;
import com.cine.demo.model.Shift;
import com.cine.demo.model.enums.ShiftStatus;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.ShiftRepository;
import com.cine.demo.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<ShiftResponseDTO> findAll() {
        return shiftRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public ShiftResponseDTO findById(Long id) {
        return shiftRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
    }

    @Override
    public List<ShiftResponseDTO> findByDate(LocalDate date) {
        return shiftRepository.findByShiftDate(date).stream().map(this::toDto).toList();
    }

    @Override
    public List<ShiftResponseDTO> findByDateRange(LocalDate from, LocalDate to) {
        return shiftRepository.findByShiftDateBetween(from, to).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public ShiftResponseDTO save(ShiftRequestDTO dto) {
        Employee employee = findEmployeeOrThrow(dto.getEmployeeId());
        Shift shift = Shift.builder()
                .employee(employee)
                .shiftDate(dto.getShiftDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .notes(dto.getNotes())
                .status(dto.getStatus() != null ? ShiftStatus.valueOf(dto.getStatus()) : ShiftStatus.SCHEDULED)
                .build();
        return toDto(shiftRepository.save(shift));
    }

    @Override
    @Transactional
    public ShiftResponseDTO update(Long id, UpdateShiftRequestDTO dto) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
        if (dto.getEmployeeId() != null) shift.setEmployee(findEmployeeOrThrow(dto.getEmployeeId()));
        if (dto.getShiftDate() != null) shift.setShiftDate(dto.getShiftDate());
        if (dto.getStartTime() != null) shift.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) shift.setEndTime(dto.getEndTime());
        if (dto.getNotes() != null) shift.setNotes(dto.getNotes());
        if (dto.getStatus() != null) shift.setStatus(ShiftStatus.valueOf(dto.getStatus()));
        return toDto(shiftRepository.save(shift));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!shiftRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shift not found with id: " + id);
        }
        shiftRepository.deleteById(id);
    }

    private Employee findEmployeeOrThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
    }

    private ShiftResponseDTO toDto(Shift s) {
        Employee emp = s.getEmployee();
        return ShiftResponseDTO.builder()
                .id(s.getId())
                .employeeId(emp != null ? emp.getId() : null)
                .employeeName(emp != null ? emp.getName() : null)
                .employeeEmail(emp != null ? emp.getEmail() : null)
                .employeeRole(emp != null && emp.getRole() != null ? emp.getRole().name() : null)
                .shiftDate(s.getShiftDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .notes(s.getNotes())
                .status(s.getStatus() != null ? s.getStatus().name() : null)
                .createdAt(s.getCreatedAt())
                .build();
    }
}
