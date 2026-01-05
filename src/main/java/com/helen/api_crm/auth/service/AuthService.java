package com.helen.api_crm.auth.service;


import com.helen.api_crm.auth.Repository.UserRepository;
import com.helen.api_crm.auth.dto.LoginRequestDTO;
import com.helen.api_crm.auth.dto.LoginResponseDTO;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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

    // Busca usuário pelo email
    public LoginResponseDTO login(LoginRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Login not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("Passwords don't match");
        }

        // Gera token
        String token = jwtService.generateToken(user.getEmail());

        // Cria DTO de Resposta
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }
}
