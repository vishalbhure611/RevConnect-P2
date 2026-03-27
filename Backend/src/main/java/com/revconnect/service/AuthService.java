package com.revconnect.service;

import com.revconnect.dto.CreateUserRequestDTO;
import com.revconnect.dto.LoginRequestDTO;
import com.revconnect.dto.UserResponseDTO;

public interface AuthService {

    String login(LoginRequestDTO request);

    UserResponseDTO register(CreateUserRequestDTO request);
}