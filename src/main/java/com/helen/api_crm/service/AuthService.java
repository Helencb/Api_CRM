package com.helen.api_crm.service;

import com.helen.api_crm.dto.LoginRequestDTO;
import com.helen.api_crm.entity.User;
import com.helen.api_crm.repository.UserRepository;
import com.helen.api_crm.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(LoginRequestDTO dto) {

        User user = repository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Passwords don't match");
        }

        return jwtService.generateToken(user.getUsername());
    }
}
