package com.helen.api_crm.auth.controller;
import com.helen.api_crm.auth.dto.LoginResponseDTO;
import com.helen.api_crm.auth.service.AuthService;
import com.helen.api_crm.auth.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        }
    }
