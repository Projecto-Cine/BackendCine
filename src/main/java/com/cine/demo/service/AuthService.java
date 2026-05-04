package com.cine.demo.service;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.dto.response.UserResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    UserResponseDTO me(String token);
}