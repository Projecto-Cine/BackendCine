package com.cine.demo.service;

import com.cine.demo.dto.request.ClientRegisterRequestDTO;
import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.dto.response.UserSummaryDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    LoginResponseDTO register(ClientRegisterRequestDTO dto);
    UserSummaryDTO me(Long userId);
}