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
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un trabajador con el email: " + dto.getEmail());
        }
        return employeeMapper.toResponseDto(
                employeeRepository.save(employeeMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public EmployeeResponseDTO update(Long id, UpdateEmployeeRequestDTO dto) {
        Employee employee = findOrThrow(id);
        if (dto.getEmail() != null && !dto.getEmail().equals(employee.getEmail())
                && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un trabajador con el email: " + dto.getEmail());
        }
        employeeMapper.updateEntityFromDto(dto, employee);
        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trabajador no encontrado con id: " + id);
        }
        if (shiftRepository.existsByEmployeeId(id)) {
            throw new ConflictException("No se puede eliminar el trabajador porque tiene turnos asignados");
        }
        employeeRepository.deleteById(id);
    }

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador no encontrado con id: " + id));
    }
}
