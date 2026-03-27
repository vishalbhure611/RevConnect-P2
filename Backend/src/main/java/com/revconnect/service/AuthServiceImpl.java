package com.revconnect.service;

import com.revconnect.dto.CreateUserRequestDTO;
import com.revconnect.dto.LoginRequestDTO;
import com.revconnect.dto.UserResponseDTO;
import com.revconnect.entity.User;
import com.revconnect.exception.ConflictException;
import com.revconnect.exception.UnauthorizedException;
import com.revconnect.repository.UserRepository;
import com.revconnect.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ================= LOGIN =================
    @Override
    public String login(LoginRequestDTO request) {

        User user = userRepository
                .findByEmail(request.getIdentifier())
                .or(() -> userRepository.findByUsername(request.getIdentifier()))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    // ================= REGISTER =================
    @Override
    public UserResponseDTO register(CreateUserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }
}