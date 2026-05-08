package com.cine.demo.service;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAll();
    UserResponseDTO getById(Long id);
    UserResponseDTO create(UserRequestDTO dto);
    UserResponseDTO update(Long id, UpdateUserRequestDTO dto);
    void delete(Long id);
    UserResponseDTO uploadImage(Long id, MultipartFile file);
}
