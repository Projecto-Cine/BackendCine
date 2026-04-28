package com.cine.demo.service.impl;

import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public List<UserResponseDTO> findAll() { return null; }

    @Override
    public UserResponseDTO findById(Long id) { return null; }

    @Override
    public UserResponseDTO save(UserRequestDTO dto) { return null; }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
