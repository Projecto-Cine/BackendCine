package com.cine.demo.service;

import com.cine.demo.dto.request.QuickRegisterDTO;
import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAll(Boolean member);
    List<UserResponseDTO> getClients();
    UserResponseDTO getById(Long id);
    UserResponseDTO create(UserRequestDTO dto);
    UserResponseDTO update(Long id, UpdateUserRequestDTO dto);
    void delete(Long id);
    UserResponseDTO uploadImage(Long id, MultipartFile file);

    List<UserResponseDTO> search(String q);
    List<UserResponseDTO> searchClients(String q);

    UserResponseDTO findByEmail(String email);
    UserResponseDTO quickRegister(QuickRegisterDTO dto);
}
