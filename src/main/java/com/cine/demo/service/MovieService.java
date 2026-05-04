package com.cine.demo.service;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MovieService {

    List<MovieResponseDTO> findAll();

    List<MovieResponseDTO> findActive();

    MovieResponseDTO findById(Long id);

    MovieResponseDTO save(MovieRequestDTO dto, MultipartFile image);

    MovieResponseDTO update(Long id, MovieRequestDTO dto);

    void delete(Long id);
}