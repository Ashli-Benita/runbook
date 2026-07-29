package com.runbookagent.service;

import com.runbookagent.dto.AuthResponseDto;
import com.runbookagent.dto.LoginRequestDto;
import com.runbookagent.dto.RegisterRequestDto;
import com.runbookagent.entity.UserEntity;
import com.runbookagent.repository.UserRepository;
import com.runbookagent.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email '" + dto.getEmail() + "' is already registered");
        }

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("ROLE_USER")
                .build();

        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        UserEntity user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponseDto me(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return AuthResponseDto.builder()
                .token(null)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
