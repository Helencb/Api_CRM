package com.helen.api_crm.auth.service;

import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.auth.dto.LoginRequestDTO;
import com.helen.api_crm.auth.dto.LoginResponseDTO;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.refreshToken.service.RefreshTokenService;
import com.helen.api_crm.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    // Teste de login bem-sucedido
    @Test
    void shouldLoginSucessfully() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@crm.com");
        user.setPassword("admin");
        user.setRole(Role.MANAGER);

        when(userRepository.findByEmail("admin@crm.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("admin", user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(any(Map.class), any())).thenReturn("mocked-jwt-token");

        when(refreshTokenService.createRefreshToken("admin@crm.com")).thenReturn("mocked-refresh-token");

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@crm.com");
        request.setPassword("admin");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
    }

    // Teste de falha de login quando a senha for inválida
    @Test
    void shouldFailLoginWhenPasswordInvalid() {
        User user = new User();
        user.setPassword("admin");

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(new LoginRequestDTO()));
    }

    // Teste de falha de login quando o usuário não for encontrado
    @Test
    void shouldFailLoginWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@crm.com"))
                .thenReturn(Optional.empty());

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("unknown@crm.com");
        request.setPassword("any");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
    }

}
