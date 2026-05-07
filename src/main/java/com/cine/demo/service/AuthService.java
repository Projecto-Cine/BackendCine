package com.cine.demo.service;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO dto);
    AuthResponseDTO register(UserRequestDTO dto);
}
