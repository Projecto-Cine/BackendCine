package com.cine.demo.service;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.request.UpdateMovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MovieService {
    List<MovieResponseDTO> getAll();
    MovieResponseDTO getById(Long id);
    MovieResponseDTO create(MovieRequestDTO dto);
    MovieResponseDTO update(Long id, UpdateMovieRequestDTO dto);
    void delete(Long id);
    MovieResponseDTO uploadPoster(Long id, MultipartFile file);
}
