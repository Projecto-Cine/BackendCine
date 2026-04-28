package com.cine.demo.service.impl;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    @Override
    public List<SeatResponseDTO> findAll() { return null; }

    @Override
    public SeatResponseDTO findById(Long id) { return null; }

    @Override
    public SeatResponseDTO save(SeatRequestDTO dto) { return null; }

    @Override
    public SeatResponseDTO update(Long id, SeatRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
