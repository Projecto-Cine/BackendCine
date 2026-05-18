package com.cine.demo.service.impl;

import com.cine.demo.dto.request.EmployeeRequestDTO;
import com.cine.demo.dto.request.UpdateEmployeeRequestDTO;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.EmployeeMapper;
import com.cine.demo.model.Employee;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.ShiftRepository;
import com.cine.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponseDto)
                .toList();
    }

    @Override
    public EmployeeResponseDTO findById(Long id) {
        return employeeMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public EmployeeResponseDTO save(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByEmail(dto.email())) {
            throw new ConflictException("An employee already exists with email: " + dto.email());
        }
        return employeeMapper.toResponseDto(
                employeeRepository.save(employeeMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public EmployeeResponseDTO update(Long id, UpdateEmployeeRequestDTO dto) {
        Employee employee = findOrThrow(id);
        if (dto.email() != null && !dto.email().equals(employee.getEmail())
                && employeeRepository.existsByEmail(dto.email())) {
            throw new ConflictException("An employee already exists with email: " + dto.email());
        }
        employeeMapper.updateEntityFromDto(dto, employee);
        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        if (shiftRepository.existsByEmployeeId(id)) {
            throw new ConflictException("Cannot delete employee because they have assigned shifts");
        }
        employeeRepository.deleteById(id);
    }

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }
}
