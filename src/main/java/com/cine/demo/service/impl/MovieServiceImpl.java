package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    @Override
    public List<MovieResponseDTO> findAll() { return null; }

    @Override
    public MovieResponseDTO findById(Long id) { return null; }

    @Override
    public MovieResponseDTO save(MovieRequestDTO dto) { return null; }

    @Override
    public MovieResponseDTO update(Long id, MovieRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
