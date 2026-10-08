package com.cine.demo.service;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.RegisterRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO dto);

    LoginResponseDTO employeeLogin(LoginRequestDTO dto);

    /**
     * Alta de un cliente desde la tienda. Devuelve la misma forma que el login,
     * con token incluido, para que el alta deje la sesion ya iniciada y el
     * cliente pueda continuar su compra sin un paso extra.
     */
    LoginResponseDTO register(RegisterRequestDTO dto);
}
