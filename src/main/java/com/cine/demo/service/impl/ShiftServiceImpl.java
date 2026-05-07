package com.cine.demo.service.impl;

import com.cine.demo.dto.request.ShiftRequestDTO;
import com.cine.demo.dto.request.UpdateShiftRequestDTO;
import com.cine.demo.dto.response.ShiftResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Shift;
import com.cine.demo.model.enums.ShiftStatus;
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

    @Override
    public List<ShiftResponseDTO> findAll() {
        return shiftRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public ShiftResponseDTO findById(Long id) {
        return shiftRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + id));
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
        Shift shift = Shift.builder()
                .employeeName(dto.getEmployeeName())
                .position(dto.getPosition())
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
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + id));
        if (dto.getEmployeeName() != null) shift.setEmployeeName(dto.getEmployeeName());
        if (dto.getPosition() != null) shift.setPosition(dto.getPosition());
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
            throw new ResourceNotFoundException("Turno no encontrado con id: " + id);
        }
        shiftRepository.deleteById(id);
    }

    private ShiftResponseDTO toDto(Shift s) {
        return ShiftResponseDTO.builder()
                .id(s.getId())
                .employeeName(s.getEmployeeName())
                .position(s.getPosition())
                .shiftDate(s.getShiftDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .notes(s.getNotes())
                .status(s.getStatus().name())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
