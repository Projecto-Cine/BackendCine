package com.cine.demo.service;

import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import java.util.List;

public interface UserService {
    List<UserResponseDTO> findAll();
    UserResponseDTO findById(Long id);
    UserResponseDTO save(UserRequestDTO dto);
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);
}
