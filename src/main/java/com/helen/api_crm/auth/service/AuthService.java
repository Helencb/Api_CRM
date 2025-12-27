package com.helen.api_crm.auth.service;


import com.helen.api_crm.auth.dto.LoginRequestDTO;
import com.helen.api_crm.auth.dto.LoginResponseDTO;
import com.helen.api_crm.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        var user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Login not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Passwords don't match");
        }

        return jwtService.generateToken(user.getUsername());
    }
}
