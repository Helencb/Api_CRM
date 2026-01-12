package com.helen.api_crm.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    String role;
    String name;
    String token;
}
