package com.cine.demo.service.impl;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    @Override
    public List<ScreeningResponseDTO> findAll() { return null; }

    @Override
    public ScreeningResponseDTO findById(Long id) { return null; }

    @Override
    public ScreeningResponseDTO save(ScreeningRequestDTO dto) { return null; }

    @Override
    public ScreeningResponseDTO update(Long id, ScreeningRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
