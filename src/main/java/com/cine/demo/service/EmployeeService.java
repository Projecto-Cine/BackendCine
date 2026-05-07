package com.cine.demo.service;

import com.cine.demo.dto.request.EmployeeRequestDTO;
import com.cine.demo.dto.request.UpdateEmployeeRequestDTO;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> findAll();
    EmployeeResponseDTO findById(Long id);
    EmployeeResponseDTO save(EmployeeRequestDTO dto);
    EmployeeResponseDTO update(Long id, UpdateEmployeeRequestDTO dto);
    void delete(Long id);
}
