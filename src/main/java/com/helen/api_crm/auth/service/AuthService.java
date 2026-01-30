package com.helen.api_crm.auth.service;


import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.auth.dto.LoginRequestDTO;
import com.helen.api_crm.auth.dto.LoginResponseDTO;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.refreshToken.dto.RefreshTokenRequestDTO;
import com.helen.api_crm.refreshToken.dto.RefreshTokenResponseDTO;
import com.helen.api_crm.refreshToken.model.RefreshToken;
import com.helen.api_crm.refreshToken.service.RefreshTokenService;
import com.helen.api_crm.security.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(null);
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("Falha de login para o email: {}", dto.getEmail());
            throw new BusinessException("Invalid email or password");
        }

        if (!user.isActive()) {
            log.warn("Falha de login para o email: {}", dto.getEmail());
            throw new BusinessException("Invalid email or password");
        }

        refreshTokenService.deleteByUserId(user.getId());

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getId());
        String token = jwtService.generateToken(extraClaims, user.getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setRefreshToken(refreshToken.getToken());
        response.setRole(user.getRole().name());
        response.setName(user.getEmail());

        return response;
    }

    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        return java.util.Optional.of(refreshTokenService.findByToken(request.getRefreshToken()))
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Map<String, Object> extraClaims = new HashMap<>();
                    extraClaims.put("role", user.getRole().name());
                    extraClaims.put("userId", user.getId());
                    String token = jwtService.generateToken(extraClaims, user.getEmail());

                    return new RefreshTokenResponseDTO(token, request.getRefreshToken());
                })
                .orElseThrow(() -> new BusinessException("Refresh token is not in database!"));
    }
}
