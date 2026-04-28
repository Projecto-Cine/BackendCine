package com.cine.demo.service.impl;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    @Override
    public List<TheaterResponseDTO> findAll() { return null; }

    @Override
    public TheaterResponseDTO findById(Long id) { return null; }

    @Override
    public TheaterResponseDTO save(TheaterRequestDTO dto) { return null; }

    @Override
    public TheaterResponseDTO update(Long id, TheaterRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
