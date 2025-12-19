package com.helen.api_crm.controller;

import com.helen.api_crm.dto.LoginRequestDTO;
import com.helen.api_crm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
public String login(@RequestBody LoginRequestDTO dto) {
        return authService.login(dto);
    }
}
