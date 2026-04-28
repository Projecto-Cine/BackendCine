package com.cine.demo.service;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import java.util.List;

public interface MovieService {
    List<MovieResponseDTO> findAll();
    MovieResponseDTO findById(Long id);
    MovieResponseDTO save(MovieRequestDTO dto);
    MovieResponseDTO update(Long id, MovieRequestDTO dto);
    void delete(Long id);
}
