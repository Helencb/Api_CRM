package com.helen.api_crm.auth.controller;
import com.helen.api_crm.auth.dto.CreateUserRequest;
import com.helen.api_crm.auth.dto.LoginResponseDTO;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.auth.service.AuthService;
import com.helen.api_crm.auth.dto.LoginRequestDTO;
import com.helen.api_crm.auth.service.UserCreationService;
import com.helen.api_crm.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserCreationService userCreationService;

    public AuthController(AuthService authService, UserCreationService userCreationService) {
        this.authService = authService;
        this.userCreationService = userCreationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }

    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userCreationService.createUser(
                request.email(),
                request.password(),
                request.role()
        );
        return ResponseEntity.ok(user);
    }
}
